new Vue({
    el:"#root",
    data:{
        brandList:[],
        brand:{
            name:'',
            firstChar:''
        },
        clearbrand:{//用来清空输入框
            name:'',
            firstChar:''
        },
        page:1,//当前页码
        pageSize:5,//一页多少条数据
        total:0,//总记录数
        maxPage:9,//最大页面数
        idList:[],//存放多炫框选中的id
        ischecked:'',//全选全不选判断
        searchBrand:{//搜索
            name:'',
            firstChar:''
        }
    },
    methods:{
        findAllBrand:function () {//请求所有的品牌数据
            var _this=this;//不能再axios里面用this
            axios.get("/brand/findAllBrands.do").then(function (response) {
                console.log(response.data);
                _this.brandList=response.data;
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        pageHandler:function (page) {
            this.page=page;
            var _this=this;
            axios.post("/brand/findPage.do?page="+this.page+"&pageSize="+this.pageSize,this.searchBrand).then(function (response) {
                _this.total=response.data.total;
                _this.brandList=response.data.rows;
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        brandSave:function () {
            //获取数据，发送请求添加品牌
            var _this=this;
            var url=''
            if (this.brand.id==null){
                url='/brand/addBrand.do';
            } else {
                url='/brand/updateBrand.do'
            }

            axios.post(url,this.brand).then(function (response) {
                if (response.data.result) {
                    alert(response.data.message);
                    _this.pageHandler(1);
                }else {
                    alert(response.data.message)
                    _this.pageHandler(1);
                }
                _this.clear();
            }).catch(function (reason) {
                console.log(reason);
                _this.clear();
            })

        },
        edit:function (brand) {
            var _this=this;
            axios.post("/brand/removeBind.do",brand).then(function (response) {//解决数据回显双向绑定问题
                _this.brand=response.data;
            })
        },
        clear:function () {//清空编辑框内容
            this.brand=this.clearbrand;
        },
        checkboxClick:function (event,id) {
            if(event.target.checked){
                this.idList.push(id);
            }else {
                var index=this.idList.indexOf(id);
                this.idList.splice(index,1);
            }
            console.log(this.idList);
        },
        deleteBrand:function () {
            if (this.idList.length==0){
                alert("请选择要删除品牌");
                return;
            }
            var _this=this;
            axios.post("/brand/deleteBrand.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
                if (response.data.result) {
                    alert(response.data.message);
                    _this.ischecked='';
                    _this.idList=[];
                    _this.pageHandler(1);
                }else {
                    alert(response.data.message)
                    _this.pageHandler(1);
                }
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        selectAll:function (event) {
            var _this=this;
            if(event.target.checked){
                this.ischecked='checked';
                this.idList=[];
                this.brandList.forEach(function (e) {
                    _this.idList.push(e.id);
                })
            }else {
                this.ischecked='';
                this.idList=[];
            }
        }
    },
    created:function () {//Vue对象初始化完成后调用
        this.pageHandler(1);
    }
})