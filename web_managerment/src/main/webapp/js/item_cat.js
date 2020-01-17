Vue.component('v-select', VueSelect.VueSelect);
new Vue({
    el: "#root",
    data:{
        categoryList:[],
        id:'',
        page:1,//当前页码
        pageSize:5,//一页多少条数据
        total:0,//总记录数
        maxPage:9,//最大页面数
        searchCat:{},//搜索框
        grade:1, /*当前级别*/
        entity1:{}, /*面包屑导航1*/
        entity2:{}, /*面包屑导航2*/

        categoryList1:[],//分类1数据列表
        categoryList2:[],//分类2数据列表
        editGrade:1,
        catSelected1:-1,//分类1选中的id
        catSelected2:-1,//分类2选中的id

        tempList:[],//添加时模板列表展示
        tempEntity:'',
        placeholder:'请选择模板',
        name:'',
        itemCatEntity:{},

        /*编辑时做数据回显用*/
        newItemCat:{},

        /*删除*/
        idList:[],//存放多炫框选中的id
        ischecked:''//全选全不选判断
    },
    methods: {
        pageHandler:function (page) {
            this.page=page;
            var _this=this;
            axios.post("/itemCat/findPage.do?page="+this.page+"&pageSize="+this.pageSize+"&parentId="+this.id,this.searchCat).then(function (response) {
                _this.total=response.data.total;
                _this.categoryList=response.data.rows;
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        selectCateByParentId: function (id) {
            this.id=id;
            this.pageHandler(this.page);
        },
        setGrade:function(value){
            this.grade = value;
        },
        nextGrade:function (entity) { //当前点击的分类
            if (this.grade == 1){//如果当前是第一级, 面包屑导航为空
                this.entity1 = {};
                this.entity2 = {};
            }
            if (this.grade == 2){ //第2级,把当前的分类显示在第1个面包屑上
                this.entity1 = entity;
                this.entity2 = {};
            }
            if (this.grade == 3){ //第3级,把当前点击分类显示到第二个面包屑上
                this.entity2 = entity;
            }
            this.selectCateByParentId(entity.id);
        },
        loadCateData: function (id) {
            _this = this;
            axios.post("/itemCat/findByParentId.do?parentId="+id)
                .then(function (response) {
                    if (_this.editGrade == 1){
                        //取服务端响应的结果
                        _this.categoryList1 = response.data;
                    }
                    if (_this.editGrade == 2){
                        //取服务端响应的结果
                        _this.categoryList2 =response.data;
                    }
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        getCatSelected:function (grade) {//选项改变时调用
            if(grade == 1){ //第1级选项改变
                this.categoryList2 = [];//清空二级分类数据
                this.catSelected2=-1;   //默认选择
                this.editGrade = grade+1; // 加载第2级的数据
                this.loadCateData(this.catSelected1);
            }
        },
         selectAllTemp: function () {/*查询所有模板*/
            axios.post("/temp/selectAll.do").then(function (response) {
                        _this.tempList = response.data;
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        itemCatSave: function () {
            this.itemCatEntity.name=this.name;
            this.itemCatEntity.type_id=this.tempEntity.id;
            if (this.catSelected1==-1){
                this.itemCatEntity.parent_id=0;
            } else if (this.catSelected2==-1){
                this.itemCatEntity.parent_id=this.catSelected1;
            } else {
                this.itemCatEntity.parent_id=this.catSelected2;
            }
            var _this=this;
            var url=''
            if (this.itemCatEntity.id==null){
                url='/itemCat/addItemCat.do';
            } else {
                url='/itemCat/updateItemCat.do';
            }
            axios.post(url,this.itemCatEntity).then(function (response) {
                if (response.data.result) {
                    alert(response.data.message);
                    _this.pageHandler(1);
                }else {
                    alert(response.data.message)
                    _this.pageHandler(1);
                }
                 _this.cleardata();
            }).catch(function (reason) {
                console.log(reason);
                 _this.cleardata();
            })
        },
        cleardata:function () {
            this.tempEntity='';
            this.name='';
            this.catSelected1=-1;
            this.catSelected2=-1;
            this.entity1={};
            this.entity2={};
            this.itemCatEntity={};
            this.grade=1;
            this.editGrade=1;
            this.page=1;
            this.selectCateByParentId(0);
        },
        showDetail:async function (item) {//回显数据
            var newItem=JSON.parse(JSON.stringify(item));
            this.name=newItem.name;//回显分类名称
            this.itemCatEntity.id=newItem.id;//分类ID
            var _this=this;
            this.tempList.forEach(function (e) {
                if(newItem.typeId==e.id){
                    _this.tempEntity=e;//类型模板ID
                }
            });
            if (newItem.parentId==0){//回显上级id（分三种情况）
            }else {
                var index=false;
                this.categoryList1.forEach(function (e) {
                    if (e.id==newItem.parentId) {
                        _this.editGrade=2
                        _this.catSelected1=e.id;
                        _this.loadCateData(e.id);
                        index=true;
                    }
                })
                if (!index) {
                    this.editGrade=2
                    await this.findParent(newItem.parentId);
                    var seccond=this.newItemCat;
                    await this.findParent(seccond.parentId);
                    var first=this.newItemCat;
                    console.log(seccond)
                    console.log(first)
                    await this.loadCateData(first.id);
                    this.catSelected1=first.id;
                    this.catSelected2=seccond.id;
                }
            }
        },
        findParent:async function (id) {
            var _this=this;
            await axios.post("/itemCat/findParent.do?parentId="+id).then(function (response) {
                _this.newItemCat=response.data;
                console.log(_this.newItemCat)
            }).catch(function (reason) {
            })
        },
        checkboxClick:function (event,id) {//多选框的点击
            if(event.target.checked){
                this.idList.push(id);
            }else {
                var index=this.idList.indexOf(id);
                this.idList.splice(index,1);
            }
            console.log(this.idList);
        },
        selectAll:function (event) {//全选全部选
            var _this=this;
            if(event.target.checked){
                this.ischecked='checked';
                this.idList=[];
                this.categoryList.forEach(function (e) {
                    _this.idList.push(e.id);
                })
            }else {
                this.ischecked='';
                this.idList=[];
            }
            console.log(this.idList);
        },
        delectItemCat:function () {
            if (this.idList.length==0){
                alert("请选择要删除的分类");
                return;
            }
            var _this=this;
            axios.post("/itemCat/delectItemCat.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
                if (response.data.result) {
                    alert(response.data.message);
                    _this.ischecked='';
                    _this.idList=[];
                    _this.pageHandler(1);
                }else {
                    alert(response.data.message)
                }
            }).catch(function (reason) {
                console.log(reason);
            })

        }
    },
    created: function () {
        this.selectCateByParentId(0);
        this.loadCateData(0);
        this.selectAllTemp();
    }
});