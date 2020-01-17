new Vue({
    el:"#root",
    data:{
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 0, //记录总数
        maxPage:0,
        searchEntity:{},
        categoryList:[],
        idList:[],//存放多炫框选中的id
        ischecked:'',//全选全不选判断

        categoryEntity:{}
    },
    methods:{
        pageHandler: function (page) {
            var _this = this;
            this.page = page;
            axios.post("/contentCat/findPage.do?page="+this.page+"&pageSize="+this.pageSize,this.searchEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.categoryList = response.data.rows;
                    _this.total = response.data.total;
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        saveCategory:function () {
            var url="";
            if (this.categoryEntity.id != null){
                url = "/contentCat/update.do";
            } else {
                url = "/contentCat/add.do"
            }
            var _this = this;
            axios.post(url, this.categoryEntity)
                .then(function (value) {
                    console.log(value.data);
                    //刷新页面
                    _this.pageHandler(1);
                    _this.categoryEntity = {};
                });
        },
        showContentCat:function (contentCat) {
            this.categoryEntity = JSON.parse(JSON.stringify(contentCat));
        },
        clearContentCat:function () {
            this.categoryEntity={};
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
        deleteContentCat:function () {
            if (this.idList.length==0){
                alert("请选择要删除的广告分类");
                return;
            }
            var _this=this;
            axios.post("/contentCat/delete.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
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
    created: function() {
        this.pageHandler(1);
    },
});