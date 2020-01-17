new Vue({
    el:"#root",
    data:{
        specList:[],//存放查询出来·的规格集合
        specEntity:{
            specification:{},
            specOptionList:[]
        },
        page:1,//当前页码
        pageSize:5,//一页多少条数据
        total:0,//总记录数
        maxPage:9,//最大页面数
        searchSpec:{//搜索
            specName:''
        },
        idList:[],//存放多炫框选中的id
        ischecked:''//全选全不选判断
    },
    methods:{
        pageHandler:function (page) {
            this.page=page;
            var _this=this;
            axios.post("/spec/findPage.do?page="+this.page+"&pageSize="+this.pageSize,this.searchSpec).then(function (response) {
                _this.total=response.data.total;
                _this.specList=response.data.rows;
            }).catch(function (reason) {
                console.log(reason)
            })
        },
        addRow:function () {
            this.specEntity.specOptionList.push({});
        },
        deleteRow:function (index) {
            this.specEntity.specOptionList.splice(index,1);
        },
        specSave:function () {
            var url='';
            if (this.specEntity.specification.id==null){
                url='/spec/specSave.do';
            }else {
                url='/spec/specUpdate.do';
            }
            var _this=this;
            axios.post(url,this.specEntity).then(function (response) {
                if (response.data.result) {
                    alert(response.data.message);
                    _this.clearSpecEntity();
                    _this.pageHandler(1);
                }else {
                    alert(response.data.message)
                    _this.pageHandler(1);
                }
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        findSpecWithId:function (id) {
            var _this=this;
            axios.get("/spec/findSpecWithId.do?id="+id).then(function (response) {
                _this.specEntity=response.data;
            }).catch(function (reason) {
                console.log(reason);
            })
        },
        clearSpecEntity:function () {
            this.specEntity.specification={};
            this.specEntity.specOptionList=[];
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
                this.specList.forEach(function (e) {
                    _this.idList.push(e.id);
                })
            }else {
                this.ischecked='';
                this.idList=[];
            }
            console.log(this.idList);
        },
        specDelect:function () {
            if (this.idList.length==0){
                alert("请选择要删除品牌");
                return;
            }
            var _this=this;
            axios.post("/spec/specDelect.do",Qs.stringify({idx:this.idList},{indices:false})).then(function (response) {
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
    created:function () {//Vue对象初始化完成后调用
        this.pageHandler(1);
    }
})