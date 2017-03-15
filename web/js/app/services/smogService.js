(function () {
    var _smogService = function (smogResourceService) {
        var _smog = {};
        
        _smog.getData = function (idCounty) {
            return smogResourceService.get({ id: idCounty }).$promise;
        };
        
        _smog.percentage = function (value, acceptableThreshold) {
            return Math.round(value / acceptableThreshold * 100);
        };
        
        return _smog;
    };
    
    angular.module('services').factory('smogService', ['smogResourceService', _smogService]);
})();