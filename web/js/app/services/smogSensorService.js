(function () {
    var _smogSensorService = function (smogSensorResourceService) {
        var _smog = {};
        
        _smog.getData = function () {
            return smogSensorResourceService.get().$promise;
        };
        
        _smog.percentage = function (value, acceptableThreshold) {
            return Math.round(value / acceptableThreshold * 100);
        };
        
        return _smog;
    };
    
    angular.module('services').factory('smogSensorService', ['smogSensorResourceService', _smogSensorService]);
})();