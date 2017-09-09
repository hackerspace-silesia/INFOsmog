(function () {
    var _smogSensorResourceService = function ($resource, apiUrlService) {
        return $resource(apiUrlService('sensor'));
    };

    angular.module('services').factory('smogSensorResourceService', ['$resource', 'apiUrlService', _smogSensorResourceService()]);
})();