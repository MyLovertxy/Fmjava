new Vue({
    el:"#root",
    data:{
        categoryList1:[],//分类1数据列表
        categoryList2:[],//分类2数据列表
        categoryList3:[],//分类3数据列表
        grade:1,  //记录当前级别
        catSelected1:-1,//分类1选中的id
        catSelected2:-1,//分类2选中的id
        catSelected3:-1,//分类3选中的id,

        typeId:0,//模板id
        brandList:[],
        selBrand:-1,

        curImageObj:{
            color:'',
            url:''
        },
        imageList:[],

        specList:[],//从服务器获取的所有规格列表,

       /* specSelList:[//记录当前选中的规格和规格选项,
            {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
            {"specName":"套餐","specOptions":["套餐1","套餐2"]}
        ],*/
        specSelList:[],//记录当前选中的规格和规格选项,

        rowList:[],

        isEnableSpec:1, //是否启用规格

        goodsEntity:{//最终保存商品的实体
            goods:{},
            goodsDesc:{},
            itemList:[]
        }
    },
    methods:{
        getQueryString:function(name) {
            //匹配以 &name=开头 或者 name=开头 中间为任意多个除了 & 以外的字符 一旦遇到 & 或者 空白 就中止取值
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");//i : 执行对大小写不敏感的匹配
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
        getCatSelected:function (grade) {//选项改变时调用
            if(grade == 1){ //第1级选项改变
                this.categoryList2 = [];//清空二级分类数据
                this.catSelected2=-1;   //默认选择
                this.categoryList3 = []; //清空三级分类数据
                this.catSelected3=-1; //默认选择
                this.grade = grade+1; // 加载第2级的数据
                this.typeId=0;
                this.loadCateData(this.catSelected1);
            }
            if(grade == 2) { //第2级选项改变
                this.categoryList3 = [];//清空三级分类数据
                this.catSelected3=-1;//默认选择
                this.grade = grade + 1;// 加载第3级的数据
                this.typeId=0;
                this.loadCateData(this.catSelected2);
            }
            if (grade == 3){ //第3级选项改变
                //加载模板
                if ((this.catSelected3==-1)){
                    this.typeId=0;
                    return;
                }
                _this = this;
                axios.post("/itemCat/findParent.do?parentId="+this.catSelected3)
                    .then(function (response) {
                        _this.typeId=response.data.typeId;
                    }).catch(function (reason) {
                    console.log(reason);
                })
            }
        },
        uploadFile:function () {
            var _this=this;
            var formData=new FormData();
            formData.append("file",file.files[0]);
            var instance = axios.create({
               withCredentials:true
            });
            instance.post("/upload/uploadFile.do",formData).then(function (response) {
                if(response.data.result){
                    _this.curImageObj.url=response.data.message;
                }else {
                    alert(response.data.message);
                }

            }).catch(function (reason) {
                console.log(reason);
            })
        },
        saveImage:function () {
            if(this.curImageObj.color==''||this.curImageObj.url==''){
                alert("请输入完整信息");
                return;
            }
            var obj = {color:this.curImageObj.color,url:this.curImageObj.url};//避免双向绑定冲突
            this.imageList.push(obj);
            this.curImageObj.color='';
            this.curImageObj.url='';
        },
        deleteImage:function (url,index) {
            //发送删除图片请求
            var _this=this;
            axios.get("/upload/deleteImage.do?url="+url).then(function (respose) {
                if(respose.data.result){
                    alert(respose.data.message)
                    _this.imageList.splice(index,1);
                }else {
                    alert(respose.data.message)
                }
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        /* specSelList:[//记录当前选中的规格和规格选项,
            {"specName":"选择颜色","specOptions":["秘境黑","星云紫"]},
            {"specName":"套餐","specOptions":["套餐1","套餐2"]}
        ],*/
        updateSpecState:function (event,specName,optionName) {
            var obj = this.searchObjectWithKey(this.specSelList,"specName",specName);
            if (obj != null){
                if(event.target.checked){
                    //选中
                    obj.specOptions.push(optionName);
                }else {//移除
                    var index = obj.specOptions.indexOf(optionName);
                    obj.specOptions.splice(index,1);

                    /*判断选项当中是否还有对象*/
                    if(obj.specOptions.length == 0){
                        var idx = this.specSelList.indexOf(obj);
                        this.specSelList.splice(idx,1);
                    }
                }
            } else {
                this.specSelList.push({"specName":specName,"specOptions":[optionName]});
            }
            this.updateRowList();
        },
        /**
         * 从集合中查询某一个key是否存在
         * 如果已经存在，返回对应的对象
         * 如果不存在返回null
         */
        searchObjectWithKey:function (list,key,value) {
            for (var i = 0; i < list.length; i++) {
                if (list[i][key] == value){
                    return list[i];
                }
            }
            return null;
        },
        updateRowList:function () {
            var rowList=[
                {spec:{},price:0,num:9999,status:'0',isDefault:'0'}
            ];
            for (var i = 0; i < this.specSelList.length; i++) {
                var specObj = this.specSelList[i];
                var specName = specObj.specName;
                var specOptions = specObj.specOptions;
                var newRowList = [];
                for (var j = 0; j < rowList.length; j++) {
                    var oldRow = rowList[j];

                    for (var k = 0; k < specOptions.length; k++) {
                       var newRow = JSON.parse(JSON.stringify(oldRow));
                       newRow.spec[specName] = specOptions[k];
                       newRowList.push(newRow);
                    }
                }
                rowList = newRowList;
            }
            this.rowList = rowList;
            console.log(this.rowList)
        },
        saveGoods:function () {
            this.goodsEntity.goods.category1Id = this.catSelected1;
            this.goodsEntity.goods.category2Id = this.catSelected2;
            this.goodsEntity.goods.category3Id = this.catSelected3;
            this.goodsEntity.goods.typeTemplateId=this.typeId,
            this.goodsEntity.goods.brandId=this.selBrand,
            this.goodsEntity.goods.isEnableSpec=this.isEnableSpec,

            this.goodsEntity.goodsDesc.itemImages=this.imageList,
            this.goodsEntity.goodsDesc.specificationItems=this.specSelList,
            this.goodsEntity.goodsDesc.introduction=UE.getEditor('editor').getContent()

            this.goodsEntity.itemList = this.rowList;


            var id = this.getQueryString("id");
            var url = '';
            if (id != null){
                url = '/goods/updateGoods.do';
            }else {
                url = '/goods/addGoods.do';
            }
            //发送请求
            axios.post(url,this.goodsEntity)
                .then(function (response) {
                    console.log(response.data);
                    if(response.data.result){
                        alert(response.data.message);
                        location.href="goods.html";
                    }else {
                        alert(response.data.message);
                    }
                }).catch(function (reason) {
                alert(response.data.message);
            });
        },
        checkAttributeValue:function (specName,optionName) {//回显规格选中状态
            var object = this.searchObjectWithKey(this.specSelList,"specName",specName);
            if(object != null){
                if(object.specOptions.indexOf(optionName)>=0){
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }

    },
    watch:{
        typeId:function (newValue,oldValue) {
            _this = this;
            _this.brandList =[];
            _this.selBrand = -1;
            axios.post("/temp/findOne.do?id="+newValue)
                .then(function (response) {
                    console.log(response.data);
                    _this.brandList = JSON.parse(response.data.brandIds);
                    if (_this.goodsEntity.goods.brandId != null){
                        _this.selBrand = _this.goodsEntity.goods.brandId;
                    }
                }).catch(function (reason) {
                console.log(reason);
            });
            _this.specList=[];
            axios.get("/temp/findSpecListBytempId.do?id="+newValue)
                .then(function (response) {
                    console.log(response.data);
                    _this.specList = response.data;
                }).catch(function (reason) {
                console.log(reason);
            });
        }
    },
    created: function() {
        this.loadCateData(0);
    },
    mounted:function () {
        var id = this.getQueryString("id");
        var _this = this;
        if (id != null){
            //根据id查询当前商品
            axios.get("/goods/findOne.do?id="+id).then(function (response) {
                //1.回显商品
                _this.goodsEntity.goods = response.data.goods;
                //2.商品描述
                _this.goodsEntity.goodsDesc = response.data.goodsDesc;
                //3.分类模板
                _this.typeId = response.data.goods.typeTemplateId;
                //回显图片
                _this.imageList = JSON.parse(response.data.goodsDesc.itemImages);
                //回显富文本
                UE.getEditor("editor").ready(function () {
                    UE.getEditor("editor").setContent(response.data.goodsDesc.introduction);
                });
                //选中规格
                _this.specSelList  = JSON.parse(response.data.goodsDesc.specificationItems);
                //库存列表
                _this.rowList = response.data.itemList;
                for (var i = 0; i< _this.rowList.length; i++){
                    _this.rowList[i].spec =  JSON.parse(_this.rowList[i].spec);
                }
                _this.catSelected1 = response.data.goods.category1Id;
                //控制选项默认选中状态
                if (response.data.goods.category1Id >= 0){
                    _this.grade  = 2
                    _this.loadCateData(_this.catSelected1);
                    _this.catSelected2 = response.data.goods.category2Id;

                    if (_this.catSelected2 >= 0){
                        axios.post("/itemCat/findByParentId.do?parentId="+_this.catSelected2)
                            .then(function (response) {
                                //取服务端响应的结果
                                _this.categoryList3 = response.data;
                            }).catch(function (reason) {
                            console.log(reason);
                        })
                        _this.catSelected3 = response.data.goods.category3Id;
                    }
                }

                }).catch(function (reason) {
                console.log(reason);
            });
        }
    }
});
