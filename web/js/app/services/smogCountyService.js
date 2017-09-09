(function () {
    var _smogCountyService = function (smogCountyResourceService) {
        var _smog = {};

        _smog.getData = function (idCounty) {
            return smogCountyResourceService.get({ id: idCounty }).$promise;
        };

        _smog.percentage = function (value, acceptableThreshold) {
            return Math.round(value / acceptableThreshold * 100);
        };

        return _smog;
    };

    angular.module('services').factory('smogCountyService', ['smogCountyResourceService', _smogCountyService]);
})();