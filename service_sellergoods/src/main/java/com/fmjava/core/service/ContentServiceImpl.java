package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.Exception.ImageException;
import com.fmjava.consts.ResComConsts;
import com.fmjava.core.dao.ad.ContentDao;
import com.fmjava.core.pojo.ad.Content;
import com.fmjava.core.pojo.ad.ContentQuery;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.utils.FastDFSClient;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;


@Service
@Slf4j
public class ContentServiceImpl implements ContentService{
    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public PageResult findPage(Content content, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        if (content != null) {
            if (content.getTitle() != null && !"".equals(content.getTitle())) {
                criteria.andTitleLike("%"+content.getTitle()+"%");
            }
        }
        Page<Content> contentList = (Page<Content>)contentDao.selectByExample(query);
        return new PageResult(contentList.getTotal(), contentList.getResult());
    }

    @Override
    public void add(Content content) {
        //1. 将新广告添加到数据库中
        contentDao.insertSelective(content);
        //2.更新redis，其实是删除下次取得时候重新加载就好了
        redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).delete(content.getCategoryId());
    }

    @Override
    public void update(Content content) {
        //更新redis老的新的都要跟新
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());
        redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).delete(oldContent.getCategoryId());
        redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).delete(content.getCategoryId());

        contentDao.updateByPrimaryKeySelective(content);
    }

    @Override
    public void delete(Long[] idx,String fileServerUrl) throws ImageException {
        for (Long id : idx) {
            Content content = contentDao.selectByPrimaryKey(id);
            String pic = content.getPic();
            try {
                deleteImage(pic,fileServerUrl);
                contentDao.deleteByPrimaryKey(id);
                //2.更新redis，其实是删除下次取得时候重新加载就好了
                redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).delete(content.getCategoryId());
            } catch (Exception e) {
                throw new ImageException("图片删除失败");
            }
        }
    }

    @Override
    public List<Content> findByCategoryId(Long id) {
        ContentQuery query = new ContentQuery();
        ContentQuery.Criteria criteria = query.createCriteria();
        criteria.andCategoryIdEqualTo(id);
        List<Content> list = contentDao.selectByExample(query);
        return list;
    }

    @Override
    public List<Content> findByCategoryIdFromRedis(Long categoryId) {
        List<Content> contentList = (List<Content>)redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).get(categoryId);
        if (null == contentList){
            contentList = findByCategoryId(categoryId);
            redisTemplate.boundHashOps(ResComConsts.CONTENT_LIST_REDIS).put(categoryId,contentList);
            log.debug("从数据库中取=============");
            System.out.println("从数据库中取=============");
        }
        System.out.println("从redis中取=============");
        log.debug("从redis中取=============");
        return contentList;
    }

    public void deleteImage(String url,String fileServerUrl) throws Exception {
        String path = url.substring(fileServerUrl.length());
        FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
        Integer res = fastDFSClient.delete_file(path);
        if (res==0){

        }else {
            throw new ImageException("图片删除失败");
        }
    }
}
