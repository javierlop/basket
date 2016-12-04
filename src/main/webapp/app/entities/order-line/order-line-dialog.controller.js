(function() {
    'use strict';

    angular
        .module('basketApp')
        .controller('OrderLineDialogController', OrderLineDialogController);

    OrderLineDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'OrderLine', 'Item'];

    function OrderLineDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, OrderLine, Item) {
        var vm = this;

        vm.orderLine = entity;
        vm.clear = clear;
        vm.save = save;
        vm.items = Item.query({filter: 'orderline-is-null'});
        $q.all([vm.orderLine.$promise, vm.items.$promise]).then(function() {
            if (!vm.orderLine.item || !vm.orderLine.item.id) {
                return $q.reject();
            }
            return Item.get({id : vm.orderLine.item.id}).$promise;
        }).then(function(item) {
            vm.items.push(item);
        });

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.orderLine.id !== null) {
                OrderLine.update(vm.orderLine, onSaveSuccess, onSaveError);
            } else {
                OrderLine.save(vm.orderLine, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('basketApp:orderLineUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
