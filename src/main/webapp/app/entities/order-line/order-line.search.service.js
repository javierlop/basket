(function() {
    'use strict';

    angular
        .module('basketApp')
        .factory('OrderLineSearch', OrderLineSearch);

    OrderLineSearch.$inject = ['$resource'];

    function OrderLineSearch($resource) {
        var resourceUrl =  'api/_search/order-lines/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
