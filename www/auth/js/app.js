const token = localStorage.getItem('authToken');
window.onload = function () {
    if (token) {
        fetch('/rest/auth/validate', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        }).then(response => {
            if (!response.ok) {
                throw new Error('Invalid token');
            }
            return response.json();
        }).then(data => {
            switch (data.role) {
                case "ADMIN":
                    const adminElements = document.getElementsByClassName('admin');

                    for (let i = 0; i < adminElements.length; i++) {
                        adminElements[i].style.display = 'block';
                    }
                    break;
            }
        })
            .catch(error => {
                console.error(error);
            });
    }
};
function handleLogin() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const data = {
        email: email,
        password: password
    };

    if (email && password) {
        fetch('/rest/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data)
        })
            .then(response => response.json())
            .then(data => {
                localStorage.setItem('authToken', data.token);
                window.location.href = '/main';
            })
    } else {
        alert("Please enter both username and password.");
    }
}

function handleRegister() {
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const markTypes = Array.from(document.getElementById('markTypes')).map(option => option.value);
    const data = {
        email: email,
        password: password,
        markTypes: markTypes
    };
    if (email && password) {
        fetch('/rest/auth/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(data)
        }).then((response) => {
            if (response.status === 200) {
                console.log("OK");
            } else {
                console.log(response.body);
            }
        })
    } else {
        alert("Please enter both username and password.");
    }
}