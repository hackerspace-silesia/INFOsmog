(function () {
    var _countyResourceService = function ($resource, apiUrlService) {
        return $resource(apiUrlService('county'));
    };
    
    angular.module('services').factory('countyResourceService', ['$resource', 'apiUrlService', _countyResourceService]);
})();