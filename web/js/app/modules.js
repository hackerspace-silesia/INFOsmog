(function () {
    angular.module('INFOsmog', ['controllers', 'directives']);
    
    angular.module('controllers', ['ui.bootstrap', 'directives', 'services']);
    angular.module('directives', ['ui.bootstrap', 'services']);
    angular.module('services', ['ngCookies', 'ngResource']);
})();