package com.fmjava.core.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.fmjava.Exception.ImageException;
import com.fmjava.core.dao.ad.ContentDao;
import com.fmjava.core.pojo.ad.Content;
import com.fmjava.core.pojo.ad.ContentQuery;
import com.fmjava.core.pojo.entity.PageResult;
import com.fmjava.utils.FastDFSClient;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;



@Service
public class ContentServiceImpl implements ContentService{
    @Autowired
    private ContentDao contentDao;
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
    }

    @Override
    public void update(Content content) {
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
            } catch (Exception e) {
                throw new ImageException("图片删除失败");
            }
        }
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
