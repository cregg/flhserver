$('.data-selector-draft').click(function() {
	$('.coming-soon').fadeOut(1);	
	$('.data-selector-draft').addClass('active');
	$('.data-selector-goalie').removeClass('active');
	$('#barChart').fadeIn();
	$('.data-visuals').fadeIn(1);
});

$('.data-selector-goalie').click(function() {
	$('.data-selector-goalie').addClass('active');
	$('.data-selector-draft').removeClass('active');
	$('.data-visuals').fadeOut(1);
	$('#barChart').fadeOut();
	$('.coming-soon').fadeIn(1);
});

$('.newid-button').click(function() {
	$('#barChart').addClass('hide');
	$('#barChart').fadeIn();
});


$('a[href*="#"]:not([href="#"]):not([href="#show"]):not([href="#hide"])').click(function() {
	if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
		var target = $(this.hash);
		target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
		if (target.length) {
			$('html,body').animate({
				scrollTop: target.offset().top
			}, 1000);
			return false;
		}
	}
});


$(document).ready(function() {
   $('.back-to-top').css('display', 'none');
    
    $('a.back-to-top').click(function(e){
        $('html, body').animate({scrollTop:0}, 'slow');
        e.preventDefault();
    });

    $(window).scroll(function() {
        if ($('body').offset().top < $(window).scrollTop()) {
            $('.back-to-top').slideDown('fast');
        } else {
            $('.back-to-top').slideUp('fast');
        }
    });
});