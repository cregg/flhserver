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


// data.token to get string and to store that string in local storage