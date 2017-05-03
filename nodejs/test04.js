var parameters = {
  "text1": "hello world1",
  "text2": "hello world2",
  "text3": "hello world3"
}

function main(params) {
  if (params.__ow_method == "get") {
    var inputs = "";
    for (i in parameters) {
      inputs += "<input name='" + i + "' placeholder='" + i + "' value='" + parameters[i] + "' style='width:100%; padding:10px'><br/>"
    }
    
    return {
      html: "<html><head>\
        <script src='https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js'></script>\
        <script>$(function() {\n\
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
    
  var out = {};
  for (var i in parameters) {
      out[i] = params[i];
  }
  return { out };
}

if (require.main === module) console.log(JSON.stringify(main(parameters), null, 2));

exports.main = main;
