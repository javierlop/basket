(function() {
    'use strict';

    angular
        .module('basketApp')
        .controller('OrderLineController', OrderLineController);

    OrderLineController.$inject = ['$scope', '$state', 'OrderLine', 'OrderLineSearch'];

    function OrderLineController ($scope, $state, OrderLine, OrderLineSearch) {
        var vm = this;
        
        vm.orderLines = [];
          
		  
		  
	}
})();

var btotal = 0;
