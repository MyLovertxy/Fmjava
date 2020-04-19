new Vue({
    el:"#app",
    data:{
        categoryList1:[],//分类1数据列表
        categoryList2:[],//分类2数据列表
        categoryList3:[],//分类3数据列表
        grade:1,  //记录当前级别
        catSeleted1:-1,
        catSeleted2:-1,
        catSeleted3:-1,
        typeId:0,
        brandList:[],
        selBrand:-1,

        curImageObj:{
            color:'',
            url:''
        },
        imageList:[],//图片集合
        specList:[],//规格
        /*
        specSelList:[      //记录当前选中的规格与规格选项
                   {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
                  {"specName":"选择版本","specOptions":["6G+64G"]},
                ]
       */
        specSelList:[], //当前选中的规格与规格选中
        rowList:[],
        isEnableSpec:1,
        goodsEntity:{
            goods:{},
            goodsDesc:{},
            itemList:[]
        }//最终保存商品的实体

    },
    methods:{
        getQueryString:function(name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
            var r = window.location.search.substr(1).match(reg);
            if (r!=null) return (r[2]); return null;
        },
        loadCateData: function (id) {
            _this = this;
            axios.post("/itemCat/findByParentId.do?parentId="+id)
                .then(function (response) {
                    if (_this.grade == 1){
                        //取服务端响应的结果
                        _this.categoryList1 = response.data;
                    }
                    if (_this.grade == 2){
                        //取服务端响应的结果
                        _this.categoryList2 =response.data;
                    }
                    if (_this.grade == 3){
                        //取服务端响应的结果
                        _this.categoryList3 =response.data;
                    }
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        getCategorySel:function (grade) {
            if (grade == 1){
                this.categoryList2 = [];
                this.catSeleted2 = -1;

                this.categoryList3 = [];
                this.catSeleted3 = -1;

                this.grade = grade +1;
                this.loadCateData(this.catSeleted1);
            }
            if (grade == 2){

                this.categoryList3 = [];
                this.catSeleted3 = -1;

                this.grade = grade +1;
                this.loadCateData(this.catSeleted2);
            }

            if (grade == 3){
                var _this = this;
                //处理模板
                axios.post("/itemCat/findOneCategory.do?id="+this.catSeleted3)
                    .then(function (response) {
                     _this.typeId =   response.data.typeId;
                    }).catch(function (reason) {
                    console.log(reason);
                })
            }

        },
        uploadFile:function () {
            var _this = this;
            //图片上传
            var formData = new FormData();
            formData.append("file",file.files[0]);
            var instance = axios.create({
                withCredentials:true
            });
            instance.post("/upload/uploadFile.do",formData).then(function (response) {
                console.log(response.data);
                _this.curImageObj.url = response.data.message;
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        saveImage:function () {
            if (this.curImageObj.color == '' || this.curImageObj.url == ''){
                alert("请输入颜色或上传图片");
                return;
            }
            var obj = {color:this.curImageObj.color,url:this.curImageObj.url};
            this.imageList.push(obj);
            this.curImageObj.color = '';
            this.curImageObj.url = '';
        },
        deleteImg:function (url,index) {
        var _this = this;
        //发送删除图片请求
        axios.get("/upload/deleteImg.do?url="+url).then(function (response) {
            if (response.data.success){
                alert(response.data.message);
                _this.imageList.splice(index,1);
            } else {
               alert(response.data.message);
            }
        }).catch(function (reason) {

        })


        },

    /**
         * 给定一个集合
         * 从集合当中查询某一个key是否已经存在
         * 如果已经存在 返回对应的对象
         * 如果不存在, 就返回null
         [      //记录当前选中的规格与规格选项
              {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
              {"specName":"选择版本","specOptions":["6G+64G"]},
         ]
         */
        searchObjectWithKey:function(list,key,value){  //searchObjectWithKey(list,"specName","选择颜色")
             for(var i=0; i < list.length; i++){
                 if (list[i][key] == value){
                     return list[i];
                 }
             }
             return null;
        },
        trim:function(str)
        {
            return str.replace(/(^\s*)|(\s*$)/g, "");
        },
        updateSpecState:function (event,specName,optionName) {
            specName = this.trim(specName);
            alert(specName);
            var obj = this.searchObjectWithKey(this.specSelList,"specName",specName);
            if (obj != null){
                  if (event.target.checked){ //选中
                      obj.specOptions.push(optionName);
                  } else {
                    //取消选中 把规格选项移除
                     var idx = obj.specOptions.indexOf(optionName);
                     obj.specOptions.splice(idx,1);
                     //判断 选项当中是否还有对象 ,如果没有  把规格对象移除
                     if (obj.specOptions.length == 0){
                         var idx = this.specSelList.indexOf(obj);
                         this.specSelList.splice(idx,1);
                     }
                  }
                console.log("存在");
            } else {
                /*判断规格名称是否已经存在  如果已经存在 就直接添加到对应的选项当中*/
                this.specSelList.push({"specName":specName,"specOptions":[optionName]});
            }
            this.createRowList();
        },
        /**
         所有的行
         RowList = [
             {spec:{},price:0,num:9999,status:'0',isDefault:'0'}
         ];

         1.遍历所有的选中的规格与规格选项的集合
         [
         {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
         {"specName":"选择版本","specOptions":["6G+64G","8G+128G">]},
         ]

         2.从所有行当中取出元素
         RowList = [
         {spec:{"选择颜色":"秘境黑"},price:0,num:9999,status:'0',isDefault:'0'}
         {spec:{"选择颜色":"星云紫"},price:0,num:9999,status:'0',isDefault:'0'}
         ]

         {spec:{"选择颜色":"秘境黑","选择版本":"6G+64G"},price:0,num:9999,status:'0',isDefault:'0'}
         {spec:{"选择颜色":"秘境黑","选择版本":"8G+128G"},price:0,num:9999,status:'0',isDefault:'0'}
         {spec:{"选择颜色":"星云紫","选择版本":"6G+64G"},price:0,num:9999,status:'0',isDefault:'0'}
         {spec:{"选择颜色":"星云紫","选择版本":"8G+128G"},price:0,num:9999,status:'0',isDefault:'0'}
         */
        createRowList:function () {
           var rowList = [
                {spec:{},price:0,num:9999,status:'0',isDefault:'0'}
           ];
            /**
             [{spec:{},price:0,num:9999,status:'0',isDefault:'0'}];
             [
                {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
                {"specName":"选择版本","specOptions":["6G+64G","8G+128G">]},
             ]
             ---------------------------------------------
             rowList[
              {spec:{选择颜色:秘境黑},price:0,num:9999,status:'0',isDefault:'0'}
              {spec:{选择颜色:星云紫},price:0,num:9999,status:'0',isDefault:'0'}
             ]

             newRowList = [
              {spec:{选择颜色:秘境黑,选择版本:6G+64G},price:0,num:9999,status:'0',isDefault:'0'}
              {spec:{选择颜色:秘境黑,选择版本:8G+128G},price:0,num:9999,status:'0',isDefault:'0'}
              {spec:{选择颜色:星云紫,选择版本:6G+64},price:0,num:9999,status:'0',isDefault:'0'}
              {spec:{选择颜色:星云紫,选择版本:8G+128G},price:0,num:9999,status:'0',isDefault:'0'}
             ];


             */
            for(var i = 0; i< this.specSelList.length; i++ ){
                var specObj = this.specSelList[i];
                var specName = specObj.specName; //选择版本
                var specOptions = specObj.specOptions; //["6G+64G","8G+128G"]
                var newRowList = [];
                for(var j=0; j<rowList.length; j++){
                    var oldRow = rowList[j]; //{spec:{选择颜色:星云紫},price:0,num:9999,status:'0',isDefault:'0'}
                    for(var k=0; k<specOptions.length; k++ ){
                        var newRow = JSON.parse(JSON.stringify(oldRow));
                         //{spec:{选择颜色:星云紫,选择版本:8G+128G},price:0,num:9999,status:'0',isDefault:'0'}
                         newRow.spec[specName] = specOptions[k];
                         newRowList.push(newRow);
                    }
                }
                rowList = newRowList;
            }
            this.rowList = rowList;
        },
        saveGoods:function () {

            this.goodsEntity.goods.category1Id = this.catSeleted1;
            this.goodsEntity.goods.category2Id = this.catSeleted2;
            this.goodsEntity.goods.category3Id = this.catSeleted3;

            this.goodsEntity.goods.typeTemplateId=this.typeId,
            this.goodsEntity.goods.brandId=this.selBrand,
            this.goodsEntity.goods.isEnableSpec=this.isEnableSpec,

            this.goodsEntity.goodsDesc.itemImages=this.imageList,
            this.goodsEntity.goodsDesc.specificationItems=this.specSelList,
            this.goodsEntity.goodsDesc.introduction=UE.getEditor('editor').getContent();

            this.goodsEntity.itemList = this.rowList;

            if (this.selBrand == -1){
                alert("请选择品牌")
                return;
            }
            //获取当前id
            var id = this.getQueryString("id");
            var url = '';
            if (id == null){
                //没有 添加操作
                url = '/goods/add.do';
            } else {
               //如果有 更新
                url='/goods/update.do';
            }

            //发送请求
            axios.post(url,this.goodsEntity)
                .then(function (response) {
                    console.log(response.data);
                    location.href="goods.html";
                }).catch(function (reason) {
                alert(response.data.message);
            });
        },
        checkSpecState:function (specName,specOption) {
            /**
             [
             {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
             {"specName":"选择版本","specOptions":["6G+64G","8G+128G">]},
             ]
             */
            var obj = this.searchObjectWithKey(this.specSelList,"specName",specName);
            if (obj !=null){
                if (obj.specOptions.indexOf(specOption) >= 0){
                    return true;
                } else{
                    return false;
                }
            }
        }
    },
    watch:{
        typeId:function (newValue,oldValue) {
            var _this = this;
            _this.brandList =[];
            _this.selBrand = -1;
            axios.post("/temp/findOne.do?id="+newValue)
                .then(function (response) {
                    console.log(response.data);
                    _this.brandList = JSON.parse(response.data.brandIds);
                    if (_this.goodsEntity.goods.brandId !=null){
                        _this.selBrand = _this.goodsEntity.goods.brandId;
                    }
                    console.log(_this.brandList);
                }).catch(function (reason) {
                console.log(reason);
            });

            _this.specList = [];
            //加载规格与规格选项
            axios.post("/temp/findBySpecWithID.do?id="+newValue).then(function (response) {
                _this.specList = response.data;
                console.log(_this.specList)
            }).catch(function (reason) {

            });
        }
    },

    created: function() {
        this.loadCateData(0);
    },
    mounted:function () {
        var id =this.getQueryString("id");
        var _this = this;
        if (id != null){
            //根据id查询当前商品
            axios.get("/goods/findOne.do?id="+id)
                .then(function (response) {
                    console.log(response.data);
                    //1.回显商品信息
                    _this.goodsEntity.goods = response.data.goods;
                    //2.商品描述信息
                    _this.goodsEntity.goodsDesc = response.data.goodsDesc;
                    //3.商品模板
                    _this.typeId = response.data.goods.typeTemplateId;
                    //3.1富文本回显
                    UE.getEditor("editor").ready(function () {
                        UE.getEditor("editor").setContent(response.data.goodsDesc.introduction);
                    });

                    //4.商品图片
                    _this.imageList = JSON.parse(response.data.goodsDesc.itemImages);
                    //5.选中的规格选项
                    _this.specSelList =JSON.parse(response.data.goodsDesc.specificationItems);
                    //6.库存信息
                    _this.rowList = response.data.itemList;
                    //7.取出每一个库存  把spec转成对象
                    for (var i = 0; i< _this.rowList.length; i++){
                        _this.rowList[i].spec = JSON.parse(_this.rowList[i].spec);
                    }

                    //8.分类默认选中
                    _this.catSeleted1 = response.data.goods.category1Id;
                    if (_this.goodsEntity.goods.category1Id > 0){
                      //加载二维的数据
                      _this.grade = 2;
                      _this.loadCateData(_this.catSeleted1);
                      _this.catSeleted2 = response.data.goods.category2Id
                    }

                    if (_this.goodsEntity.goods.category2Id > 0) {
                        axios.post("/itemCat/findByParentId.do?parentId="+_this.goodsEntity.goods.category2Id)
                            .then(function (response) {
                               _this.categoryList3 =response.data;
                            }).catch(function (reason) {
                            console.log(reason);
                        })
                        _this.catSeleted3 = response.data.goods.category3Id;
                    }

                }).catch(function (reason) {
                console.log(reason);
            });
        }
    }
});
