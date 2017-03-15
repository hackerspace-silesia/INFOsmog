(function () {
    var _signingInResourceService = function ($resource, apiUrlService) {
       return $resource(apiUrlService('signin'));
    };
    
    angular.module('services').factory('signingInResourceService', ['$resource', 'apiUrlService', _signingInResourceService]);
})();