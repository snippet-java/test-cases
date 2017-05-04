var parameters = {
    "text": "hello my friend",
    "fromLanguage": "en",
    "toLanguage": "es",
    "username": "",
    "password": "",
    "url": 'https://gateway.watsonplatform.net/language-translator/api'
}

function main(params) {
    if (params.__ow_method == "get") {
        var inputs = "";
        for (i in parameters) {
            inputs += "<input name='" + i + "' placeholder='" + i + "' value='" + parameters[i] + "' style='width:100%; padding:10px'><br/>"
        }

        return {
            html: "\
        <html><head>\
        <script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>\
        <script>\n\
        $(function() {\n\
            var url = window.location.href.substr(window.location.href.lastIndexOf('/') + 1);\n\
            url = url.substr(0, url.indexOf('.')) + '.json';\n\
            $('#submit').click(function() {\n\
                var data = {};\n\
                $('input').each(function(i, el) { data[$(this).attr('name')] = $(this).val(); })\n\
                $.ajax({ url, data, method: 'POST' })\n\
                .done(function(data) { $('#result').val(JSON.stringify(data,null,2)); });\n\
            })\n\
        })\n\
        </script></head><body>" + inputs + "<button id='submit' style='width: 100%; padding: 10px;'>Submit</button>\
        <textarea id='result' style='width: 100%; height: 500px'></textarea></body></html>"
        }
    }

    return new Promise(function (resolve, reject) {
        var newParams = {};
        for (var i in parameters) {
            newParams[i] = params[i]
        }
        translate(newParams, function (result) {
            return resolve(result);
        });
    });
}

function translate(params, cb) {
    const watson = require('watson-developer-cloud');

    var language_translator;

    if (params.username != null && params.username != "" && params.password != null && params.password != "" && params.url != null && params.url != "") {
        var credentials = {
            username: params.username,
            password: params.password,
            url: params.url,
            version: 'v2'
        }
        language_translator = watson.language_translator(credentials);
    } else if (process.env.services) {
        var services = JSON.parse(process.env.services);
        if (services.language_translator && services.language_translator[0] && services.language_translator[0].credentials) {
            var credentials = services.language_translator[0].credentials;
            language_translator = watson.language_translator(credentials);
        } else {
            return cb({ err: "missing credentials" });
        }
    } else {
        return cb({ err: "missing credentials" });
    }

    var options = {
        text: params.text,
        source: params.fromLanguage,
        target: params.toLanguage
    };
    language_translator.translate(options, function (err, translation) {
        if (err)
            return cb({ err });

        return cb(translation);
    });
}

if (require.main === module) {
    translate(parameters, function (result) {
        console.log(JSON.stringify(result, null, 2));
    });
}

exports.main = main;
