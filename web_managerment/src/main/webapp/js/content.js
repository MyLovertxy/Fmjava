new Vue({
    el:"#root",
    data:{
        page: 1,  //显示的是哪一页
        pageSize: 10, //每一页显示的数据条数
        total: 0, //记录总数
        maxPage:0,
        searchEntity:{},
        contentList:[],
        status:{0:"无效",1:"有效"},
        categoryList:[],
        contentEntity:{
            categoryId:-1,
            pic:'',
            status:1
        },

        idList:[],//存放多炫框选中的id
        ischecked:'',//全选全不选判断
    },
    methods:{
        pageHandler: function (page) {
            let _this = this;
            this.page = page;
            axios.post("/content/findPage.do?page="+this.page+"&pageSize="+this.pageSize,this.searchEntity)
                .then(function (response) {
                    //取服务端响应的结果
                    _this.contentList = response.data.rows;
                    _this.total = response.data.total;
                    console.log(response.data.rows)
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        loadCategoryContent:function () {
            var _this = this;
            axios.post("/contentCat/findAll.do")
                .then(function (response) {
                    //取服务端响应的结果
                    _this.categoryList = response.data;
                    console.log(response.data);
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        uploadFile:function () {
            var formData = new FormData();
            formData.append('file', file.files[0])
            const instance=axios.create({
                withCredentials: true
            });
            var _this = this;
            instance.post("/upload/uploadFile.do", formData).then(function (response) {
                console.log(response.data);
                _this.contentEntity.pic = response.data.message;
            }).catch(function (reason) {
                console.log(reason);
            });
        },
        saveContent:function () {
            var url="";
            if (this.contentEntity.id != null){
                url = "/content/update.do";
            } else {
                url = "/content/add.do"
            }
            var _this = this;
            console.log(this.contentEntity)
            axios.post(url,this.contentEntity)
                .then(function (response) {
                    if (response.data.result) {
                        alert(response.data.message);
                        _this.pageHandler(1);
                    }else {
                        alert(response.data.message)
                        _this.pageHandler(1);
                    }
                    this.clearContentData();
                }).catch(function (reason) {
                console.log(reason);
            })
        },
        clearContentData:function () {
            this.contentEntity = {
                categoryId:-1,
                pic:'',
                status:1
            }
        },
        showData:function (content) {
            var newContent = JSON.parse(JSON.stringify(content));
            this.contentEntity = newContent;
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
                this.contentList.forEach(function (e) {
                    _this.idList.push(e.id);
                })
            }else {
                this.ischecked='';
                this.idList=[];
            }
            console.log(this.idList);
        },
        deleteContent:function () {
            if (this.idList.length==0){
                alert("请选择要删除的广告");
                return;
            }
            var _this=this;
            axios.post("/content/delete.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
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
        this.loadCategoryContent();
    },
});