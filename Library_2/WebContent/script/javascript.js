$().ready(function() {
	var alignment = function() {
		var top = ($(window).height() - $(".sign").height()) / 2;
		var left = ($(window).width() - $(".sign").width()) / 2;
		$(".sign").css({top: top, left: left});
	};
	
	$("#take").click(function() {
		$("#auth").fadeToggle();
		alignment();
	});

	$("#auth").on("click", function(e) {
		if (e.delegateTarget == e.target) 
			$(e.target).fadeToggle();
	});
				
	$(window).resize(function() {
		alignment();
	});
	
	alignment();
});
	