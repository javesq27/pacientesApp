$(document).ready(function(){
    var showCompleted = false;
    var showDelayed = true;
    var showCommon = true;

    ocultar(".CtrueDtrue, .CtrueDfalse");

    $("#ctrue").click(function(){
        if (showCompleted == true){
            ocultar(".CtrueDtrue, .CtrueDfalse");
            showCompleted = false;
            $("#ctrue").html("Mostrar Completadas")
        }
        else{
            mostrar(".CtrueDtrue, .CtrueDfalse");
            showCompleted = true;
            $("#ctrue").html("Ocultar Completadas")
        }
    });

    $("#cfalsedfalse").click(function(){
        if (showCommon == true){
            ocultar(".CfalseDfalse");
            showCommon = false;
            $("#cfalsedfalse").html("Mostrar Comunes")
        }
        else{
            mostrar(".CfalseDfalse");
            showCommon = true;
            $("#cfalsedfalse").html("Ocultar Comunes")
        }
    });

    $("#cfalsedtrue").click(function(){
        if (showDelayed == true){
            ocultar(".CfalseDtrue");
            showDelayed = false;
            $("#cfalsedtrue").html("Mostrar Retrasadas")
        }
        else{
            mostrar(".CfalseDtrue");
            showDelayed = true;
            $("#cfalsedtrue").html("Ocultar Retrasadas")
        }
    });

    function ocultar(nclase){
        $(nclase).hide();
    }

    function mostrar(nclase){
        $(nclase).show();
    }
});