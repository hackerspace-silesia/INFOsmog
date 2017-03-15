(function () {
    var _apiUrlServiceConfig = function (apiUrlServiceProvider) {
        apiUrlServiceProvider.port = 80;
        apiUrlServiceProvider.relativePath = 'api/v1';
    };
    
    angular.module('services').config(['apiUrlServiceProvider', _apiUrlServiceConfig]);
})();