
new Vue({
    el:"#root",
    data:{
        searchMap:{
            'keywords':'',//搜索关键字
            'category':'',//分类
            'brand':'',//品牌
            'spec':{},//规格
            // 'price':'',//价格
            'pageNo':1,//当前页
            'pageSize':5,//每页展示多少条数据
            'sort':'',//排序
            'sortField':''//排序的字段
        },
        resultMap:{
            rows:[],
            total:0,
            totalPages:0,
        },
        pageLabel:[],//所有页码
        firstDot:true,//前面有点
        lastDot:true,//后边有点

        tiaozhuan:1//直接跳转到第几页
    },
    methods:{
        getQueryString:function (name) {
            var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)","i");
            var r = window.location.search.substr(1).match(reg);
            if (r!=null){
                return (decodeURI(r[2]));
            }
            return null;
        },
        search:function () {
            this.searchMap.pageNo= parseInt(this.searchMap.pageNo);//转换为数字
            let _this = this;
            axios.post("/itemsearch/search.do",this.searchMap)
                .then(function (response) {
                    _this.resultMap = response.data;
                    _this.buildPages(_this);
                    _this.tiaozhuan=_this.searchMap.pageNo;
                }).catch(function (reason) {
                console.log("异常");
            });
        },
        addSearchItem:function(key,value){
            //如果用户点击的是分类或品牌
            if(key=='category' || key=='brand' || key=='price'){
                this.searchMap[key]=value;

            }else{//用户点击的是规格
                Vue.set(this.searchMap.spec,key,value);
            }
            console.log(this.searchMap);
            this.search();
        },
        //撤销搜索项
        removeSearchItem:function(key){
            //如果用户点击的是分类或品牌
            if(key=='category' || key=='brand' || key=='price' ){
                this.searchMap[key]=null;
            }else{//用户点击的是规格
                Vue.delete(this.searchMap.spec,key);
            }
            this.search();
        },
        buildPages:function (obj) {
            obj.pageLabel = [];
            //定义开始页面和截止页码
            var firstPage = 1;
            var lastPage = obj.resultMap.totalPages;
            obj.firstDot = true;
            obj.lastDot = true;
            
            if (obj.resultMap.totalPages > 5){

                if (obj.searchMap.pageNo <=3){
                    obj.firstDot = false;
                    lastPage = 5;
                }else if (obj.searchMap.pageNo> obj.resultMap.totalPages-2){
                    obj.lastDot = false;
                    firstPage = obj.resultMap.totalPages-4;
                }else {
                    firstPage = obj.searchMap.pageNo-2;
                    lastPage = obj.searchMap.pageNo+2;
                }

            }else {
                obj.firstDot = false;
                obj.lastDot = false;
            }
            for (var i =firstPage;i<=lastPage;i++){
                obj.pageLabel.push(i);
            }
        },
        queryByPage:function (pageNo) {
            if (pageNo < 1|| pageNo > this.resultMap.totalPages) {
                return;
            }
            //重新加载数据
            this.searchMap.pageNo = pageNo;
            this.search();
        },
        isTopPage:function () {
            if (this.searchMap.pageNo == 1){
                return true;
            }else {
                return false;
            }
        },
        isEndPage:function () {
            if (this.searchMap.pageNo == this.resultMap.totalPages){
                return true;
            }else {
                return false;
            }
        },
        openDetailPage:function(id){
            window.open("http://localhost:8086/"+id+".html");
        }
    },
    watch: { //监听属性的变化

    },
    created: function() {//创建对象时调用

    },
    mounted:function () {//页面加载完
        let sc = this.getQueryString("sc");
        this.searchMap.keywords=sc;
        this.search();
    }
});