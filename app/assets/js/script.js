$('.data-selector-draft').click(function() {
	$('.coming-soon').fadeOut();	
	$('.data-selector-draft').addClass('active');
	$('.data-selector-goalie').removeClass('active');
	$('#barChart').fadeIn();
	$('.data-visuals').fadeIn();
});

$('.data-selector-goalie').click(function() {
	$('.data-selector-goalie').addClass('active');
	$('.data-selector-draft').removeClass('active');
	$('#barChart').fadeOut();
	$('.data-visuals').fadeOut();
	$('.coming-soon').fadeIn();
	$('.coming-soon').html("Hello");
});



/* ***************************
  Enable Smooth Scrolling
  Author: Chris Coyier
  URL:  CSS-Tricks.com
***************************** */

// Enable Smooth Scrolling ...  by Chris Coyier of CSS-Tricks.com
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