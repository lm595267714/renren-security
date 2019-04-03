$(function () {
    $('#sendExamineBeginTime').datetimepicker({
        format: "yyyy-mm-dd hh:ii",
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true
    }).on('changeDate',function(ev){
       var startDate = $("#sendExamineBeginTime").val();
       $("#sendExamineEndTime").datetimepicker("setStartDate",startDate);
    });

    $('#sendExamineEndTime').datetimepicker({
        format: "yyyy-mm-dd hh:ii",
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true
    });

    $('#reportBeginTime').datetimepicker({
        format: "yyyy-mm-dd hh:ii",
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true
    }).on('changeDate',function(ev){
        var startDate = $("#reportBeginTime").val();
        $("#reportEndTime").datetimepicker("setStartDate",startDate);
    });

    $('#reportEndTime').datetimepicker({
        format: "yyyy-mm-dd hh:ii",
        todayBtn: "linked",
        language: "zh-CN",
        autoclose: true
    });

    $("#jqGrid").jqGrid({
        url: baseURL + 'assay/list',
        datatype: "json",
        colModel: [			
			{ label: '报告单类型', name: 'reportType', index: "reportType", width: 40 },
			{ label: '条码号', name: 'barCode', width: 45, key: true },
            { label: '样本状态', name: 'sampleStatus', sortable: false, width: 40 },
			{ label: '姓名', name: 'patientName', width: 50 },
			{ label: '性别', name: 'sex', width: 20 },
            { label: '送检日期', name: 'examineTime', width: 50 },
            { label: '年龄', name: 'age', width: 20 },
            { label: '报告时间', name: 'reportTime', width: 50 },
            { label: '打印次数', name: 'printTimes', width: 40 },
            { label: '打印时间', name: 'printTime', width: 50 },
            { label: '送检医生', name: 'doctor', width: 50 },
            { label: '送检科室', name: 'department', width: 20 },
            { label: '仪器代号', name: 'instrumentNo', width: 30 },
            { label: '医院', name: 'hosptial', width: 30 }
        ],
		viewrecords: true,
        height: 280,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25,
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        loadComplete : function() {
            var grid = $("#jqGrid");
            var ids = grid.getDataIDs();
            for (var i = 0; i < ids.length; i++) {
                grid.setRowData (ids[i], true, {height: 10}); //设置成你要设定的固定行高
                //console.log(i);
            }
        },
        onSelectRow : function(ids) {
            console.log(ids);
            if (ids == null) {
                ids = 0;
                if (jQuery("#jqGridDetail").jqGrid('getGridParam', 'records') > 0) {
                    jQuery("#jqGridDetail").jqGrid(  'setGridParam',
                        {
                            url : ctx+"/assay/detail?q=1&id="
                            + ids,
                            page : 1
                        });
                    jQuery("#jqGridDetail").jqGrid('setCaption', "详情" + ids).trigger('reloadGrid');
                }
            } else {
                jQuery("#jqGridDetail").jqGrid('setGridParam', {
                    url : baseURL+"/assay/detail?q=1&id=" + ids,
                    page : 1
                });
                jQuery("#jqGridDetail").jqGrid('setCaption',
                    "详情: " + ids).trigger(
                    'reloadGrid');
            }
        }
    });

    $("#jqGridDetail").jqGrid({
        url: baseURL + '/assay/detail',
        datatype: "json",
        colModel: [
            { label: '简称', name: 'shortName', width: 30 },
            { label: '项目', name: 'itemName', width: 80 },
            { label: '结果', name: 'itemResult', width: 30 },
            { label: '提示', name: 'tips', width: 60, formatter: function(value, options, row){
                return value === 0 ?
                    '<span class="label label-danger">禁用</span>' :
                    '<span class="label label-success">正常</span>';
            }},
            { label: '参考值', name: 'reference', width: 30 }
        ],
        height: 340,
        width:600,
        rowNum: 10,
        rowList : [10,30,50],
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        pager: "#jqGridPagerDetail",
        prmNames : {
            page:"page",
            rows:"limit",
            order: "order"
        },
        caption : "详情"
    });
});
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({
    el:'#rrapp',
    data:{
        q:{
            username: null,
            sendExamineBeginTime:null,
            sendExamineEndTime: null,
            outPaientNo: null,
            doctor: null,
            reportBeginTime: null,
            reportEndTime: null,
            barCode: null,
            patientName: null,
            reportType: null,
            normal: null,
            printStatus: null,
            instrumentNo: null,
            materialStatus: null
        },
        showList: true,
        title:null,
        roleList:{},
        user:{
            status:1,
            deptId:null,
            deptName:null,
            roleIdList:[]
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
            vm.showList = false;
            vm.title = "新增";
            vm.roleList = {};
            vm.user = {deptName:null, deptId:null, status:1, roleIdList:[]};

            //获取角色信息
            this.getRoleList();

            vm.getDept();
        },
        getDept: function(){
            //加载部门树
            $.get(baseURL + "sys/dept/list", function(r){
                ztree = $.fn.zTree.init($("#deptTree"), setting, r);
                var node = ztree.getNodeByParam("deptId", vm.user.deptId);
                if(node != null){
                    ztree.selectNode(node);

                    vm.user.deptName = node.name;
                }
            })
        },
        update: function () {
            var userId = getSelectedRow();
            if(userId == null){
                return ;
            }

            vm.showList = false;
            vm.title = "修改";

            vm.getUser(userId);
            //获取角色信息
            this.getRoleList();
        },
        del: function () {
            var userIds = getSelectedRows();
            if(userIds == null){
                return ;
            }

            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/user/delete",
                    contentType: "application/json",
                    data: JSON.stringify(userIds),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                vm.reload();
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function () {
            var url = vm.user.userId == null ? "sys/user/save" : "sys/user/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.user),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            vm.reload();
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getUser: function(userId){
            $.get(baseURL + "sys/user/info/"+userId, function(r){
                vm.user = r.user;
                vm.user.password = null;

                vm.getDept();
            });
        },
        getRoleList: function(){
            $.get(baseURL + "sys/role/select", function(r){
                vm.roleList = r.list;
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择部门",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级部门
                    vm.user.deptId = node[0].deptId;
                    vm.user.deptName = node[0].name;

                    layer.close(index);
                }
            });
        },
        reload: function () {
            var startDate = $("#sendExamineBeginTime").val();
            var endDate = $("#sendExamineBeginTime").val();
            var reportStartDate = $("#reportBeginTime").val();
            var reportEndDate = $("#reportEndTime").val();
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'username': vm.q.username,
                    'sendExamineBeginTime': startDate,
                    'sendExamineEndTime': endDate,
                    'outPaientNo': vm.q.outPaientNo,
                    'doctor': vm.q.doctor,
                    'reportBeginTime': reportStartDate,
                    'reportEndTime': reportEndDate,
                    'barCode': vm.q.barCode,
                    'patientName': vm.q.patientName,
                    'reportType': vm.q.reportType,
                    'normal': vm.q.normal,
                    'printStatus': vm.q.printStatus,
                    'instrumentNo': vm.q.instrumentNo,
                    'materialStatus': vm.q.materialStatus},
                page:page
            }).trigger("reloadGrid");
        }
    }
});