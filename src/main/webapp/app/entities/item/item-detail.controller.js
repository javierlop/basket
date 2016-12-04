(function() {
    'use strict';

    angular
        .module('basketApp')
        .controller('ItemDetailController', ItemDetailController);

    ItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Item'];

    function ItemDetailController($scope, $rootScope, $stateParams, previousState, entity, Item) {
        var vm = this;

        vm.item = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('basketApp:itemUpdate', function(event, result) {
            vm.item = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
