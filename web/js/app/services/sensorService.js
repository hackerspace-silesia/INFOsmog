(function () {
    var _sensorService = function ($cookies, sensorResourceService) {
        var _sensor = {};

        _sensor.getList = function () {
            return sensorResourceService.query().$promise;
        };

        return _sensor;
    };

    angular.module('services').factory('sensorService', ['$cookies', 'sensorResourceService', _sensorService()]);
})();