var SIGN_REGEXP = /([yMdhsm])(\1*)/g;
var DEFAULT_PATTERN = 'yyyy-MM-dd';
function padding(s, len) {
    var len = len - (s + '').length;
    for (var i = 0; i < len; i++) { s = '0' + s; }
    return s;
};

export default {
    getQueryStringByName: function (name) {
        var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        var r = window.location.search.substr(1).match(reg);
        var context = "";
        if (r != null)
            context = r[2];
        reg = null;
        r = null;
        return context == null || context == "" || context == "undefined" ? "" : context;
    },
    formatDate: {
        format: function (date, pattern) {
          pattern = pattern || DEFAULT_PATTERN;
          return pattern.replace(SIGN_REGEXP, function ($0) {
            switch ($0.charAt(0)) {
              case 'y': return padding(date.getFullYear(), $0.length);
              case 'M': return padding(date.getMonth() + 1, $0.length);
              case 'd': return padding(date.getDate(), $0.length);
              case 'w': return date.getDay() + 1;
              case 'h': return padding(date.getHours(), $0.length);
              case 'm': return padding(date.getMinutes(), $0.length);
              case 's': return padding(date.getSeconds(), $0.length);
              default : return padding(date.getFullYear(), $0.length);
            }
          });
        },
        parse: function (dateString, pattern) {
          const matchs1 = pattern.match(SIGN_REGEXP);
          const matchs2 = dateString.match(/(\d)+/g);
          if (matchs1.length === matchs2.length) {
            const currentDate = new Date(1970, 0, 1);
            for (let i = 0; i < matchs1.length; i += 1) {
              const currentInt = parseInt(matchs2[i], 10);
              const sign = matchs1[i];
              switch (sign.charAt(0)) {
                case 'y': currentDate.setFullYear(currentInt); break;
                case 'M': currentDate.setMonth(currentInt - 1); break;
                case 'd': currentDate.setDate(currentInt); break;
                case 'h': currentDate.setHours(currentInt); break;
                case 'm': currentDate.setMinutes(currentInt); break;
                case 's': currentDate.setSeconds(currentInt); break;
                default : break;
              }
            }
            return currentDate;
          }
          return null;
        },
    },
};
