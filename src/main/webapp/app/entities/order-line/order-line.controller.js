(function() {
    'use strict';

    angular
        .module('basketApp')
        .controller('OrderLineController', OrderLineController);

    OrderLineController.$inject = ['$scope', '$state', 'OrderLine', 'OrderLineSearch'];

    function OrderLineController ($scope, $state, OrderLine, OrderLineSearch) {
        var vm = this;
        
        vm.orderLines = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            OrderLine.query(function(result) {
                vm.orderLines = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            OrderLineSearch.query({query: vm.searchQuery}, function(result) {
                vm.orderLines = result;
            });
        }    }
})();

var btotal = 0;
