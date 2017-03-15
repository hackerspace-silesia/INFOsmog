(function () {
    var _countyService = function ($cookies, countyResourceService) {
        var _county = {};
        
        var _idCookieKey = 'idCounty';
        
        _county.getList = function () {
            return countyResourceService.query().$promise;
        };
        
        _county.getId = function (list) {
            var _id = $cookies.get(_idCookieKey);
            _id = _id ? +_id : list[0].id;
            
            return _id;
        };
        
        _county.storeId = function (id) {
            var _expirationDate = moment();
            _expirationDate.add(1, 'y');
            
            $cookies.put(_idCookieKey, id, {
                expires: _expirationDate.toDate()
            });
        };
        
        return _county;
    };
    
    angular.module('services').factory('countyService', ['$cookies', 'countyResourceService', _countyService]);
})();