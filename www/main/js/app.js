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
                case "CUSTOMER":
                    const customerElements = document.getElementsByClassName('customer');

                    for (let i = 0; i < customerElements.length; i++) {
                        customerElements[i].style.display = 'block';
                    }
                    break;
                default:
                    const assessorElements = document.getElementsByClassName('assessor');

                    for (let i = 0; i < assessorElements.length; i++) {
                        assessorElements[i].style.display = 'block';
                    }
                    break;
            }
        })
            .catch(error => {
                console.error(error);
                window.location.href = '/auth';
            });
    } else {
        window.location.href = '/auth';
    }
};

function logout() {
    localStorage.removeItem('authToken');
    window.location.href = '/auth';
}

function hideAll() {
    const elementsToHide = document.getElementsByClassName('element');

    for (let i = 0; i < elementsToHide.length; i++) {
        elementsToHide[i].style.display = 'none';
    }
}

function markTypeShow() {
    hideAll();
    fetch('/rest/main/marks', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => response.json())
        .then(markTypes => {
            const container = document.getElementsByClassName('adminMarktype').item(0);
            container.style.display = 'block';

            markTypes.forEach(markType => {
                const card = document.createElement('div');
                card.classList.add('element');
                card.style.background = '#F2F2F2';

                card.innerHTML = `
                            <h3>${markType.id}</h3>
                            <p>${JSON.stringify(markType)}</p>
                        `;

                container.appendChild(card);
            });
        })
        .catch(error => {
            console.error('Error fetching items:', error);
        });
}

//todo replace with form
function markTypeCreate() {
    const jsonString = document.getElementById('markTypeJson').value;
    fetch('/rest/main/marks', {
        method: 'POST',
        body: jsonString,
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    })
    markTypeShow();
}

function loadShow() {
    hideAll();
    const container = document.getElementsByClassName('loadForm').item(0);
    container.style.display = 'block';
    const card = document.createElement('form');
    card.classList.add('element');
    card.id = 'loadFormData';
    card.enctype = 'multipart/form-data';
    card.method='POST'
    card.action='/rest/main/load'
    card.style.background = '#F2F2F2';

    card.innerHTML = `
        <label for="loadInput">Выберите файл:</label>
        <input type="file" id="loadInput" name="file" required><br><br>
        
        <label for="jsonInput">Настройки:</label>
        <textarea id="loadJson" name="settings" rows="5" cols="40" required></textarea><br><br>
        <button onclick="loadFunction()">Отправить</button> `;

    container.appendChild(card);
}

//todo and send create and function to assign here
function loadFunction() {
    const formElement = document.getElementById("loadFormData");
    const file = document.getElementById("loadInput").files[0];
    const settings = document.getElementById("loadJson").value;
    const formData = new FormData(/*{"file": file, "settings": settings}*/);
    formData.append("file", file);
    formData.append("settings", settings);

    alert("send fetch")
    fetch("/rest/main/load", {
        method: "POST",
        body: formData,
        headers: {
            'Content-Type': 'multipart/form-data; boundary=ABCboundary',
            'Authorization': `Bearer ${token}`
        }
    })
        .then(response => response.json())
        .then(data => {
            console.log("Ответ от сервера:", data);
        })
        .catch(error => {
            console.error("Ошибка при отправке данных:", error);
        });
}

function sourceShow() {
    hideAll();
    const container = document.getElementsByClassName('sourceContainer').item(0);
    container.style.display = 'block';
    container.style.background = '#F2F2F2';
    fetch('/rest/main/source',
        {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#sourceTable tbody');
            tableBody.innerHTML = '';

            for (let i = 0; i < data.length; i++) {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = data[i].id;
                row.appendChild(idCell);

                const descriptionCell = document.createElement('td');
                descriptionCell.textContent = data[i].description;
                row.appendChild(descriptionCell);

                const photoCell = document.createElement('td');
                photoCell.textContent = JSON.stringify(data[i].photo);
                row.appendChild(photoCell);

                const queryCell = document.createElement('td');
                queryCell.textContent = data[i].query;
                row.appendChild(queryCell);

                const titleCell = document.createElement('td');
                titleCell.textContent = data[i].title;
                row.appendChild(titleCell);

                const urlCell = document.createElement('td');
                urlCell.textContent = data[i].url;
                row.appendChild(urlCell);

                const batchSettingsCell = document.createElement('td');
                batchSettingsCell.textContent = data[i].batchSettings;
                row.appendChild(batchSettingsCell);

                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

    fetch('/rest/main/source/mapped',
        {
            method: "GET",
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#mappedSourceTable tbody');
            tableBody.style.background = '#F2F2F2';
            tableBody.innerHTML = '';

            for (let i = 0; i < data.length; i++) {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = data[i].mainSource.id;
                row.appendChild(idCell);

                const descriptionCell = document.createElement('td');
                descriptionCell.textContent = data[i].mainSource.description;
                row.appendChild(descriptionCell);

                const photoCell = document.createElement('td');
                photoCell.textContent = JSON.stringify(data[i].mainSource.photo);
                row.appendChild(photoCell);

                const queryCell = document.createElement('td');
                queryCell.textContent = data[i].mainSource.query;
                row.appendChild(queryCell);

                const titleCell = document.createElement('td');
                titleCell.textContent = data[i].mainSource.title;
                row.appendChild(titleCell);

                const urlCell = document.createElement('td');
                urlCell.textContent = data[i].mainSource.url;
                row.appendChild(urlCell);

                const batchSettingsCell = document.createElement('td');
                batchSettingsCell.textContent = JSON.stringify(data[i].mainSource.batchSettings);
                row.appendChild(batchSettingsCell);

                const idCellSecond = document.createElement('td');
                idCellSecond.textContent = data[i].attachedSource.id;
                row.appendChild(idCellSecond);

                const descriptionCellSecond = document.createElement('td');
                descriptionCellSecond.textContent = data[i].attachedSource.description;
                row.appendChild(descriptionCellSecond);

                const photoCellSecond = document.createElement('td');
                photoCellSecond.textContent = JSON.stringify(data[i].attachedSource.photo);
                row.appendChild(photoCellSecond);

                const queryCellSecond = document.createElement('td');
                queryCellSecond.textContent = data[i].attachedSource.query;
                row.appendChild(queryCellSecond);

                const titleCellSecond = document.createElement('td');
                titleCellSecond.textContent = data[i].attachedSource.title;
                row.appendChild(titleCellSecond);

                const urlCellSecond = document.createElement('td');
                urlCellSecond.textContent = data[i].attachedSource.url;
                row.appendChild(urlCellSecond);

                const batchSettingsCellSecond = document.createElement('td');
                batchSettingsCellSecond.textContent = JSON.stringify(data[i].attachedSource.batchSettings);
                row.appendChild(batchSettingsCellSecond);

                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}

function batchSettingShow() {
    hideAll();
    const container = document.getElementsByClassName('batchSettingContainer').item(0);
    container.style.display = 'block';
    container.style.background = '#F2F2F2';
    fetch('/rest/main/batch/settings', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => response.json())
      .then(data => {
            const tableBody = document.querySelector('#batchSettingsTable tbody');
            tableBody.innerHTML = '';

            for (let i = 0; i < data.length; i++) {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = data[i].id;
                row.appendChild(idCell);

                const priorityCell = document.createElement('td');
                priorityCell.textContent = data[i].priority;
                row.appendChild(priorityCell);

                const overlapCell = document.createElement('td');
                overlapCell.textContent = data[i].overlap;
                row.appendChild(overlapCell);

                const consistencyCell = document.createElement('td');
                consistencyCell.textContent = data[i].consistency;
                row.appendChild(consistencyCell);

                const markTypeCell = document.createElement('td');
                markTypeCell.textContent = JSON.stringify(data[i].markType);
                row.appendChild(markTypeCell);

                const batchTypeCell = document.createElement('td');
                batchTypeCell.textContent = data[i].batchType;
                row.appendChild(batchTypeCell);

                const statusCell = document.createElement('td');
                statusCell.textContent = data[i].status;
                row.appendChild(statusCell);

                const assignCell = document.createElement('td');
                row.appendChild(assignCell);
                const assignButton = document.createElement('div');
                assignButton.id='assignButtonId';
                assignButton.style.background='#333';
                assignButton.innerHTML = `<button>Assign</button>`
                assignCell.appendChild(assignButton);
                assignButton.addEventListener('click', function() {
                    assignBatch(data[i]);
                });
                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

}

//todo сейчас ассигнется на рандомного с подходящими правами
function assignBatch(batch) {
    let userToAssign = {};
    fetch(`/rest/auth/users/${batch.batchType}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        }}
        ).then(response => response.json())
        .then(users => {
            userIdx = Math.floor(Math.random() * ((users.length -1) - 0 + 1)) + 0;
            userToAssign = users[userIdx];
            fetch(`/rest/logic/batch/assign`,{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`,
                    'batchId': `${batch.id}`,
                    'assessorId': `${users[userIdx].id}`
                }
            }).then(response => {
                if (response.ok) {
                    alert(`Assigned to ${users[userIdx].email} (пока рандом - этот алерт должен уйти)`)
                }
                return response.json();
            }) .catch(error => {
                console.error(error);
            })
});
    fetch(`/rest/logic/batch/create`,{
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            'batchId': `${batch.id}`
        }
    });
}

function scoreFunction() {
    window.location.href = '/logic';
}