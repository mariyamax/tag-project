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
                alert("Not ok")
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
        elementsToHide[i].innerHTML = '';
        elementsToHide[i].style.display = 'none';
    }
}

function scoreFunction() {
    window.location.href = '/logic';
}


function homeShow() {
    hideAll();
    fetch('/rest/logic/users/personal', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => response.json())
        .then(userPersonalResponse => {
            const container = document.getElementsByClassName('personalPage').item(0);
            container.style.display = 'block';

            const card = document.createElement('div');
            card.classList.add('element');
            card.style.background = '#F2F2F2';
            card.innerHTML = `
                            <h5>Количество Оценок:${userPersonalResponse.userAchievement.scoresAmount}</h5>
                            <h5>Точность:${userPersonalResponse.userAchievement.accuracy} (на основании ${userPersonalResponse.userAchievement.accuracyAmount} проверок) </h5>
                            <button id="userBatchButton">Показать Батчи</button>
                            <div>
                            <table id="userBatchTable">
                                <thead>
                                    <tr>
                                        <th>id</th>
                                        <th>priority</th>
                                        <th>overlap</th>
                                        <th>consistency</th>
                                        <th>markType</th>
                                        <th>batchType</th>
                                        <th>status</th>
                                    </tr>
                                </thead>
                                <tbody>

                                </tbody>
                            </table>
                            </div>
                        `;

            container.appendChild(card);
            const userBatchButton = document.getElementById("userBatchButton")
            userBatchButton.addEventListener('click', function() {
                fetchBatches(userPersonalResponse.assignedBatch, userPersonalResponse.userAchievement.userId);
            });
        })
        .catch(error => {
            console.error('Error fetching items:', error);
        });
}


function fetchBatches(batchIds, userId) {
    const data = {
        userId: userId,
        batchIds: batchIds
    };
    fetch('/rest/main/batch/settings/user', {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#userBatchTable tbody');
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

                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });

}

function showScore() {
    hideAll();
    var aviableBatches = [];
    var userId = -1;
    fetch('/rest/logic/users/personal', {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => response.json())
        .then(userPersonalResponse => {
            aviableBatches = userPersonalResponse.assignedBatch;
            userId = userPersonalResponse.userAchievement.userId;
            const data = {
                userId: userId,
                batchIds: aviableBatches
            };

            fetch('/rest/main/batch/settings/user', {
                method: 'POST',
                body: JSON.stringify(data),
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                }
            }).then(response => response.json())
                .then(data => {
                    const container =  document.getElementsByClassName('scorePage').item(0);
                    container.style.display = 'block';
                    container.style.background = container.style.background = '#F2F2F2';
                    for (let i = 0; i < data.length; i++) {
                        const card = document.createElement('div');
                        card.innerHTML = `<li><button>Start ${data[i].id}</button></li>`;
                        card.addEventListener('click', function() {
                            startScore(data[i], data[i].markType, userId);
                        });
                        container.appendChild(card);
                    }
                }).catch(error => {
                console.error('Error fetching data:', error);
            });
        })
}

function startScore(batch, markType, userId) {
    hideAll();
    const container = document.getElementsByClassName('scorePage').item(0);
    container.style.alignContent='center';
    container.style.display = 'block';
    container.style.background = container.style.background = '#F2F2F2';
    const markTypeElement = document.createElement('div');
    markTypeElement.id='markTypeElement';
    markTypeElement.innerHTML = `
        <h3>${markType.id}</h3>
    `;
    if (markType.grade.multiply) {
        const markTypeGrade = document.createElement('select');
        markTypeGrade.id='scoredMarkTypeGrade';
        for (const value of markType.grade.attributes) {
            markTypeGrade.innerHTML += `
             <option class="markTypeGrade" value="${value}">${value}</option>
            `
        }
        markTypeElement.appendChild(markTypeGrade);
    } else {
        const markTypeGrade = document.createElement('div');
        markTypeGrade.id='scoredMarkTypeGrade';
        for (const value of markType.grade.attributes) {
            markTypeGrade.innerHTML += `
             <input type="radio" class="markTypeGrade" value="${value}">${value}</input>
            `
        }
        markTypeElement.appendChild(markTypeGrade);
    }
    if (markType.entity.multiply) {
        const markTypeEntity = document.createElement('select');
        markTypeEntity.id='scoredMarkTypeEntity';
        for (const value of markType.entity.attributes) {
            markTypeEntity.innerHTML += `
             <option class="markTypeEntity" value="${value}">${value}</option>
            `
        }
        markTypeElement.appendChild(markTypeEntity);
    } else {
        const markTypeEntity = document.createElement('div');
        markTypeEntity.id='scoredMarkTypeEntity';
        for (const value of markType.entity.attributes) {
            markTypeEntity.innerHTML += `
             <input type="radio" class="markTypeEntity" value="${value}">${value}</input>
            `
        }
        markTypeElement.appendChild(markTypeEntity);
    }
    container.appendChild(markTypeElement);
    var sourcesElements = [];
    if (batch.batchType=='MATCHING') {
        fetch(`/rest/main/source/mapped/${batch.id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        }).then(response => response.json())
            .then(data => {
                sourcesElements = data;
            })
    } else {
        fetch(`/rest/main/source/${batch.id}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        }).then(response => response.json())
            .then(data => {
                sourcesElements = data;
            });
    }

    container.innerHTML += `<p>Введи номер</p><input type="number" id="sourceIdInput" value="1"" />`;
    const sourceIdInput = document.getElementById('sourceIdInput');
        sourceIdInput.addEventListener('change', function() {
            const batchRemovedElement = document.getElementById("batchElementDiv");
            if (batchRemovedElement) {
                batchRemovedElement.remove();
            }
            const batchElement = document.createElement('div');
            batchElement.id="batchElementDiv";
            batchElement.innerHTML = '';
            batchElement.style.background='#F2F2F2';
            //todo add elements and multiply photo
            if (batch.batchType=='MATCHING') {
                mainSourceFoto = sourcesElements[sourceIdInput.value].mainSource.photo[0];
                attachedSourceFoto = sourcesElements[sourceIdInput.value].attachedSource.photo[0];
                batchElement.innerHTML = `
                <h5>${sourcesElements[sourceIdInput.value].mainSource.title}<h5>
                <h5>${sourcesElements[sourceIdInput.value].mainSource.description}<h5>
                <img src="${mainSourceFoto}" alt="${mainSourceFoto}" width="200" height="200"/>
                <h5>--------------------------------------------------------<h5>
                <h5>${sourcesElements[sourceIdInput.value].attachedSource.title}<h5>
                <h5>${sourcesElements[sourceIdInput.value].attachedSource.description}<h5>
                <img src="${attachedSourceFoto}" alt="${attachedSourceFoto}" width="200" height="200"/>
                <br>
                <button id="sendBatchMarkTypeButton" >SEND BTACH MARKTYPE</button>
                `;
                /*scoreSource(${sourcesElements[sourceIdInput.value].id}
                * card.addEventListener('click', function() {
                            startScore(data[i], data[i].markType, userId);
                        });
                        container.appendChild(card);*/
            } else {
                mainSourceFoto = sourcesElements[sourceIdInput.value].photo[0];
                batchElement.innerHTML = `
                <h5>${sourcesElements[sourceIdInput.value].title}<h5>
                <h5>${sourcesElements[sourceIdInput.value].description}<h5>
                <img src="${mainSourceFoto}" alt="${mainSourceFoto}" width="200" height="200"/>
                <br>
                <button id="sendBatchMarkTypeButton" >SEND BTACH MARKTYPE</button>
                `;
            }
            container.appendChild(batchElement);
            const sendBatchMarkTypeButton = document.getElementById("sendBatchMarkTypeButton")
            sendBatchMarkTypeButton.addEventListener('click', function() {
                scoreSource(batch.id, sourcesElements[sourceIdInput.value].id, markType.entity.multiply, markType.grade.multiply);
            });
        });
}


function scoreSource(batchId, sourceId, multiplyEntity, multiplyGrade) {
    const markTypeElement = document.getElementById('markTypeElement');
    var entity = [];
    var grade = [];
    if (multiplyEntity) {
        const scoredMarkTypeEntity = document.getElementById('scoredMarkTypeEntity');
        for (var option of scoredMarkTypeEntity.selectedOptions) {
            entity.push(option.value);
        }
    } else {
        const radios = document.getElementsByName('markTypeEntity');
        for (var radio of radios) {
            if (radio.checked) {
                entity.push(radio.value);
                break;
            }
        }
    }
    if (multiplyGrade) {
        const scoredMarkTypeGrade = document.getElementById('scoredMarkTypeGrade');
        for (var option of scoredMarkTypeGrade.selectedOptions) {
            grade.push(option.value);
        }
    } else {
        const radios = document.getElementsByName('markTypeGrade');
        for (var radio of radios) {
            if (radio.checked) {
                grade.push(radio.value);
                break;
            }
        }
    }
    const data = {
        entity: entity,
        grade: grade,
        sourceId: sourceId
    };
    fetch(`/rest/logic/score`, {
        method: 'POST',
        body: JSON.stringify(data),
        headers: {
            'Content-Type': 'application/json',
            'batchId': batchId,
            'Authorization': `Bearer ${token}`,
        }
    }).then((response) => {
        if (response.status === 200) {
            alert("Оценка отправлена успешно! (Этот алерт должен уйти)")
        } else {
            console.log(response.body);
        }
    });
}

function usersRating() {
    hideAll();
    const container = document.getElementsByClassName('personalPage').item(0);
    const table = document.createElement('div');
    table.innerHTML = `
        <table id="userPageTable">
        <thead>
        <tr>
            <th>user_id</th>
            <th>scores_amount</th>
            <th>accuracy</th>
            <th>accuracy_amount</th>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
    `;
    container.appendChild(table);
    container.style.display = 'block';
    container.style.background = '#F2F2F2';
    fetch('/rest/logic/users/rating',
        {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#userPageTable tbody');
            tableBody.innerHTML = '';

            for (let i = 0; i < data.length; i++) {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = data[i].userId;
                row.appendChild(idCell);

                const descriptionCell = document.createElement('td');
                descriptionCell.textContent = data[i].scoresAmount;
                row.appendChild(descriptionCell);

                const queryCell = document.createElement('td');
                queryCell.textContent = data[i].accuracy;
                row.appendChild(queryCell);

                const titleCell = document.createElement('td');
                titleCell.textContent = data[i].accuracyAmount;
                row.appendChild(titleCell);

                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}

function checkSettings() {
    hideAll();
    const container = document.getElementsByClassName('personalPage').item(0);
    const table = document.createElement('div');
    table.innerHTML = `
        <table id="userPageTable">
        <thead>
        <tr>
            <th>id</th>
            <th>table</th>
            <th>first</th>
            <th>second</th>
        </tr>
        </thead>
        <tbody>

        </tbody>
    </table>
    `;
    container.appendChild(table);
    container.style.display = 'block';
    container.style.background = '#F2F2F2';
    fetch('/rest/logic/settings/all',
        {
            method: "GET",
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => {
            const tableBody = document.querySelector('#userPageTable tbody');
            tableBody.innerHTML = '';

            for (let i = 0; i < data.length; i++) {
                const row = document.createElement('tr');

                const idCell = document.createElement('td');
                idCell.textContent = data[i].id;
                row.appendChild(idCell);

                const descriptionCell = document.createElement('td');
                descriptionCell.textContent = data[i].tableDefinition;
                row.appendChild(descriptionCell);

                const queryCell = document.createElement('td');
                queryCell.textContent = data[i].firstParam;
                row.appendChild(queryCell);

                const titleCell = document.createElement('td');
                titleCell.textContent = data[i].additionalParam;
                row.appendChild(titleCell);

                tableBody.appendChild(row);
            }
        })
        .catch(error => {
            console.error('Error fetching data:', error);
        });
}