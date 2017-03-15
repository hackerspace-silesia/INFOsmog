(function () {
    var _smogResourceService = function ($resource, apiUrlService) {
        return $resource(apiUrlService('county/:id'));
    };
    
    angular.module('services').factory('smogResourceService', ['$resource', 'apiUrlService', _smogResourceService]);
})();