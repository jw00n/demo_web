var main= {
	init: function(){
		var _this = this;
		
		$('#btn-userpage').on('click',function(){
			_this.userpage();
		});
		$('#btn_logout').on('click',function(){
			_this.logout();
		})
		
		
	},
	
	
	logout : function(){
		 localStorage.clear();
		 //session live 
		 //need delte
	},
	userpage: function(){
		var token = localStorage.getItem('token');
		
		$.ajax({
					url: '/api/user',
					type: 'GET',
					beforeSend: function(xhr) {
						xhr.setRequestHeader("Content-type", "application/json");
						xhr.setRequestHeader("Authorization", "Bearer " + token);
					},
					success:function(response){
						alert("check");
					}
		
				});
		}	
}