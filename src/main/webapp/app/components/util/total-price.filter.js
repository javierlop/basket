(function() {
    'use strict';

    angular
        .module('basketApp')
        .filter('totalprice', totalprice);
        
    function totalprice () {
        return function(items) {
			var total = 0, i = 0;
			for (i = 0; i < items.length; i++) {
				if (items[i].number%3!=0)
					total += items[i].total;
				else
					total += (items[i].total/3)*2;
			}	
			return total;
		  
        }
    }
})();
