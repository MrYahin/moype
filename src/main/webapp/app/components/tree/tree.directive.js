(function() {
    'use strict';

    angular.module('pdm').directive('treeDirective', TreeDirective);

    //Внедрение зависимостей
    TreeDirective.inject = ["DialogBox", "TreeFactory", "$filter", "CategoryService"];

    function TreeDirective(DialogBox, TreeFactory, $filter, CategoryService) {       
        return {
            templateUrl: '/app/components/tree/tree.html',
	        restrict: 'EA',
            link: link
        }
         
        function link(scope, element, attrs) {
            var vm = this;
            vm.filter = $filter;
            vm.products = [];
            vm.list = [];
            vm.stageList = {};
            scope.treeDetail = {};

            $('[data-toggle="tooltip"]').tooltip();

            vm.listProducts = function(){
                CategoryService.list().then(function(response){
                    vm.products = TreeFactory.getData(response.data);
                    vm.list = response.data;
                    vm.tree.treeview({
                        data: vm.products,
                        highlightSearchResults: false,
                        showTags: true
                    });
                    refreshNode(vm);
                });
            }
            vm.listProducts();

            vm.tree = element.find('#tree').treeview({
                data: vm.products,
                showTags: true
            });

            var observer = new MutationObserver(function(mutations) {
               refreshNode(vm);
            });
            
            observer.observe(element.find(".treeview")[0], {
                childList: true,
                subtree: true,
                showTags: true
            });

            function refreshNode(vm){
                var that = vm;
                var array = element.find('.treeview .list-group .node-tree');
                for (var index = 0; index < array.length; index++) {
                    var value = array[index];
                    value.getAttribute("data-nodeid");
                    value.setAttribute("data-toggle", "tooltip");
                    value.setAttribute("title", "Title" + (1));
                    value.setAttribute('data-placement', 'top');
                    $('[data-toggle="tooltip"]').tooltip();
                }
            }

            function getSelected(){
                return vm.tree.treeview('getSelected');
            }

            scope.collapseAll = function(){
                vm.tree.treeview('collapseAll');
            }

            scope.expandAll = function(){
                vm.tree.treeview('expandAll');
            }

            scope.search = function(e) {
                var pattern = element.find('#input-search').val();
                var options = {
                    ignoreCase: true,
                    revealResults: true
                };
                var search = vm.filter("searchTree")(vm.products, pattern);
                vm.tree.treeview({
                    data: vm.products,
                    highlightSearchResults: false
                });
                vm.tree.treeview('collapseAll');
                var results = vm.tree.treeview('search', [pattern, options]);
            }

            scope.removeTree = function(){
                DialogBox.show("Are you sure you want to delete this node?", "Confirm").then(function(){
                    var arrayRemove = vm.filter("searchIdsRemoveTree")(vm.products, vm.tree.treeview('getSelected')[0].idCategory);
                    CategoryService.deleteCategory(arrayRemove).then(function(data){
                        DialogBox.show("Category removed successfully!", "OK");
                        vm.listProducts();
                    });
                });
            }

            scope.saveTree = function(data){
                if(vm.tree.treeview('getSelected').length > 0 && scope.type == "Add")
                    data.idParentCategory = vm.tree.treeview('getSelected')[0].idCategory;
                
                if(data.codeCategory == null || data.descriptionCategory == null){
                    DialogBox.show("The code and description fields are mandatory", "Warning");
                    return;
                }
                
                data.statusCategory = 1;

                CategoryService.register(data).then(function(){
                    DialogBox.show("Category successfully registered", "OK");
                    data = {};
                    vm.listProducts();
                     $('#modal-add-tree').modal("hide");
                });
            }

            function mapToObj(map){
                const obj = {}
                for (let [k,v] of map)
                    obj[k] = v
                return obj
            }

            scope.addStages = function(){



                var stages = new Map();
                var stagePoint = new Map();

                stagePoint.set('number','1');
                stagePoint.set('name','Этап 1');
                stagePoint.set('needTime','3');

                stages.set('stages', mapToObj(stagePoint));

                let data = {order: "1", data: mapToObj(stages)}

                var selectedElem = vm.tree.treeview('getSelected')[0].idCategory;
                if(!selectedElem){
                    DialogBox.show("Select a node to add stage", "Warning");
                    return;
                }

               CategoryService.addStage(JSON.stringify(data)).then(function(data){
                   DialogBox.show("Stages added", "OK");
               });
            }

            scope.modalTree = function(type){
                scope.treeDetail = {};
                scope.type = type;


                if(type == "Edit"){
                    var selected = vm.tree.treeview('getSelected')[0];

                    if(!selected){
                        DialogBox.show("Select a node to edit", "Warning");
                        return;
                    }

                    scope.treeDetail.idParentCategory = selected.idParentCategory;
                    scope.treeDetail.descriptionCategory = selected.text;
                    scope.treeDetail.noteCategory = selected.noteCategory;
                    scope.treeDetail.codeCategory = selected.codeCategory;
                    scope.treeDetail.idCategory = selected.idCategory;

                    if(getSelected().length == 0){
                        DialogBox.show("You need to select an item from the list to edit", "Warning");
                        return;
                    }
                }

                if(type == "addStages") {

                    var selected = vm.tree.treeview('getSelected')[0];
                    CategoryService.listOfStage(selected.codeCategory).then(function(response){
                        vm.stageList = response.data;
                    });

                    scope.stageList = {codeNom:selected.codeCategory, stages:vm.stageList};

                    $('#dialog-add-stage').modal({
                        keyboard: false,
                        backdrop: 'static'
                    });
                }


                if(type == "Add") {
                    $('#modal-add-tree').modal({
                        keyboard: false,
                        backdrop: 'static'
                    });
                }
            }

            scope.cancelTree = function(){
                scope.treeDetail = {};
                $('#modal-add-tree').modal("hide");
            }

            scope.cancelAddStages = function(){
                $('#dialog-add-stage').modal("hide");
            }

            scope.saveStages = function(){

                var rows = [];
                $('#stages tr').each(function (i, n) {
                    if ($(this).find('td:eq(1)').text() != "" ) {
                        rows.push({
                            id: $(this).find('td:eq(0)').text(),
                            number: $(this).find('td:eq(1)').text(),
                            name: $(this).find('td:eq(2)').text(),
                            needTime: $(this).find('td:eq(3)').text(),
                            division: $(this).find('td:eq(4)').text()
                        });
                    }
                    if ($(this).find('td:eq(1) input').val() != undefined ) {
                        rows.push({
                            number: $(this).find('td:eq(1) input').val(),
                            name: $(this).find('td:eq(2) input').val(),
                            needTime: $(this).find('td:eq(3) input').val(),
                            division: $(this).find('td:eq(4) input').val(),
                        });
                    }
                });

                var selected = vm.tree.treeview('getSelected')[0];
                scope.stageList = {codeNom:selected.codeCategory, stages:rows};

                CategoryService.addStages(scope.stageList).then(function(){
                    DialogBox.show("Stages successfully registered", "OK");
                    $('#dialog-add-stage').modal("hide");
                });
            }
        }
    }
})();