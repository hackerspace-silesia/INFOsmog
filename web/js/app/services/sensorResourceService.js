(function () {
    var _sensorResourceService = function ($resource, apiUrlService) {
        return $resource(apiUrlService('sensor'));
    };

    angular.module('services').factory('sensorResourceService', ['$resource', 'apiUrlService', _sensorResourceService()]);
})();