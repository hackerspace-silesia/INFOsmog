(function () {
    var _apiUrlService = function () {
        var _this = this;
        
        _this.port;
        _this.relativePath;
        
        _this.$get = ['$location', function ($location) { 
            return function (serviceName) {
                var _urlWithoutProtocol = $location.host() + ':' + _this.port + '/' + _this.relativePath + '/' + serviceName;
                
                return 'http://' + _urlWithoutProtocol.replace(/\/\//g, '/');
            };
        }];
    };
    
    angular.module('services').provider('apiUrlService', _apiUrlService);
})();