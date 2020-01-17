Vue.component('v-select', VueSelect.VueSelect);
new Vue({
    el:"#root",
    data:{
        tempList:[],//存放查询出来模板集合
        page:1,//当前页码
        pageSize:5,//一页多少条数据
        total:0,//总记录数
        maxPage:9,//最大页面数
        searchTemp:{},//搜索
        name:'',
        /*品牌下拉列表初始化*/
        placeholder:'可进行多选',
        selectBrands: [],
        brandsOptions: [],//查询出来的下拉列表选项集合

        /*规格下拉列表初始化*/
        specOptions: [],
        selectSpecs: [],

        /*模板实体*/
        tempEntity:{
            name:'',
            brandIds:[],
            specIds:[]
        },

        /*批量删除*/
        idList:[],//存放多选框选中的id
        ischecked:'',//全选全不选判断
        isSelall:false
    },
    methods:{
        pageHandler:function (page) {
            this.clearChecked();
            this.page=page;
            var _this=this;
            axios.post("/temp/findPage.do?page="+this.page+"&pageSize="+this.pageSize,this.searchTemp).then(function (response) {
                _this.total=response.data.total;
                _this.tempList=response.data.rows;
                console.log(_this.tempList);
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        /*json转字符串，josnStr：json数据，key：要转成字符串的属性*/
        josnToStr:function (josnStr,key) {
           var jsonObj = JSON.parse(josnStr);
           var value='';
           for (var i = 0;i < jsonObj.length;i++){
               if(i>0){
                   value += ","
               }
               value += jsonObj[i][key];
           }
            return value;
        },
        selected_brand: function(values){
            if (values!=null) {
                this.selectBrands =values.map(function(obj){
                    return obj.id
                });
            }

        },
        selected_spec: function(values){
            if(values!=null){
                this.selectSpecs =values.map(function(obj){
                    return obj.id
                });
            }
        },
        selLoadData:function () {
            _this = this;
            axios.get("/brand/selectOptionList.do")
                .then(function (response) {
                    _this.brandsOptions = response.data;
                }).catch(function (reason) {
                console.log(reason);
            });
            axios.post("/spec/selectOptionList.do").then(function (response) {
                    _this.specOptions = response.data;
                    console.log(_this.specOptions);
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        tempSave:function () {
            var _this = this;
            var url='';
            if(this.tempEntity.id==null){
                url="/temp/tempSave.do";
            }else {
                url="/temp/tempUpdate.do";
            }
            axios.post(url,this.tempEntity).then(function (response) {
                    if (response.data.result) {
                        alert(response.data.message);
                        _this.pageHandler(1);
                        _this.clearTempData();
                    }else {
                        alert(response.data.message)
                    }
                }).catch(function (reason) {
                console.log(reason);
            });
        },
        tempEdit:function(tempEntity){
            var _this=this;
            axios.post("/temp/tempEdit.do",tempEntity)
                .then(function (response) {
                    _this.tempEntity.id=response.data.id;
                    _this.tempEntity.name = response.data.name;
                    _this.tempEntity.brandIds=JSON.parse(response.data.brandIds);
                    _this.tempEntity.specIds=JSON.parse(response.data.specIds);

                }).catch(function (reason) {
                console.log(reason);
            });
        },
        clearTempData:function () {
            this.tempEntity={};
            this.selectBrands=[];
            this.selectSpecs=[];
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
                this.tempList.forEach(function (e) {
                    _this.idList.push(e.id);
                })
            }else {
                this.ischecked='';
                this.idList=[];
            }
            console.log(this.idList);
        },
        tempDelect:function () {
            if (this.idList.length==0){
                alert("请选择要删除品牌");
                return;
            }
            var _this=this;
            axios.post("/temp/tempDelect.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
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

        },
        clearChecked(){
            this.isSelall=false;
            this.ischecked='';
            this.idList=[];
        }
    },
    created:function () {//Vue对象初始化完成后调用
        this.pageHandler(1);
        this.selLoadData();
    }
})