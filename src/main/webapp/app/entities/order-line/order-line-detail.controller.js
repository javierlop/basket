(function() {
    'use strict';

    angular
        .module('basketApp')
        .controller('OrderLineDetailController', OrderLineDetailController);

    OrderLineDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'OrderLine', 'Item'];

    function OrderLineDetailController($scope, $rootScope, $stateParams, previousState, entity, OrderLine, Item) {
        var vm = this;

        vm.orderLine = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('basketApp:orderLineUpdate', function(event, result) {
            vm.orderLine = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
