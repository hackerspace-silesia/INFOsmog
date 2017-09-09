(function () {
    var _smogCountyResourceService = function ($resource, apiUrlService) {
        return $resource(apiUrlService('county/:id'));
    };
    
    angular.module('services').factory('smogCountyResourceService', ['$resource', 'apiUrlService', _smogCountyResourceService]);
})();