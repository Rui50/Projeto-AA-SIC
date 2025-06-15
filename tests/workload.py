from locust import HttpUser, SequentialTaskSet, task, between, constant
import random
import json
import os
import gevent.lock

login_data = "users.csv"
register_data = "users_to_create.csv"

def login(client, credentials):
    data = {"email": credentials[0], "password": credentials[1]}
    response = client.post("/auth/login", json=data)
    if response.status_code == 200:
        token = response.json().get("token")
        userId = response.json().get("userId")
        #print("Login successful:", token)
        #print("User ID:", userId)
        return token, userId
    else:
        #print("Failed login:", response.text)
        return None

class LoginAndCheckWorkouts(SequentialTaskSet):
    wait_time = between(1, 3)

    credentials = []

    def on_start(self):
        if not self.credentials:
            with open(login_data, 'r') as file:
                self.credentials = [line.strip().split(';') for line in file.readlines()]
        self.username, self.password = random.choice(self.credentials)
        self.token, self.userId = login(self.client, (self.username, self.password))

    @task
    def check_workouts(self):
        response = self.client.get(
            f"/workout-plans/user/{self.userId}",
            cookies={"token": self.token},
            name="/workouts"
        )
        if response.status_code == 200:
            pass
        else:
            print("Failed to retrieve workouts:", response.text)


class LoginAndCheckProgress(SequentialTaskSet):
    wait_time = between(1, 3)

    credentials = []

    def on_start(self):
        if not self.credentials:
            with open(login_data, 'r') as file:
                self.credentials = [line.strip().split(';') for line in file.readlines()]

        self.username, self.password = random.choice(self.credentials)

    @task(1)
    def login_task(self):
        token, userId = login(self.client, (self.username, self.password))
        self.token = token
        self.userId = userId

    
    @task(2)
    def check_progress(self):
        response = self.client.post(
            f"/progress/dashboard",
            json={
                "userId": self.userId,
                "timePeriod": "all"
            },
            cookies={"token": self.token},
            name="/progress/dashboard"
        )
        if response.status_code == 200:
            #progress = response.json()
            #print("Retrieved progress:", progress)
            pass
        else:
            print("Failed to retrieve progress:", response.text)


class LoginAndCreateWorkoutPlanAndUpdate(SequentialTaskSet):
    wait_time = between(1, 3)

    credentials = []

    def on_start(self):
        if not self.credentials:
            with open(login_data, 'r') as file:
                self.credentials = [line.strip().split(';') for line in file.readlines()]

        self.username, self.password = random.choice(self.credentials)

    @task(1)
    def login_task(self):
        token, userId = login(self.client, (self.username, self.password))
        self.token = token
        self.userId = userId

    @task(2)
    def create_workout_plan(self):
        response = self.client.post(
            f"/workout-plans",
            json={
                "name": "teste",
                "ownerId": self.userId,
            },
            cookies={"token": self.token},
            name="/workout-plans"
        )

        if response.status_code == 201:
            #print("Workout plan created successfully")
            self.workout_plan_id = response.json().get("id")
            pass
        else:
            print("Failed to create workout plan:", response.text)
    """
    @task(3)
    def update_workout_plan(self):
        if not hasattr(self, 'workout_plan_id') or self.workout_plan_id is None:
            print("No workout plan ID, skipping update.")
            return

        with self.client.put(
            f"/workout-plans/{self.workout_plan_id}",
            json={
                "name": "teste",
                "description": "",
                "ownerId": str(self.userId),
                "scheduleType": "Fixed",
                "scheduledDays": [
                    "THURSDAY",
                    "MONDAY",
                    "WEDNESDAY",
                    "TUESDAY",
                    "SATURDAY",
                    "FRIDAY",
                    "SUNDAY"
                ],
                "active": False,
                "exercises": [
                    {
                        "exerciseDataId": None,
                        "exerciseId": 1,
                        "note": "",
                        "plannedSets": [
                            {
                                "id": None, 
                                "setNumber": 1,
                                "reps": 1,
                                "weight": 2.0,
                                "restTimeSugested": 2.0
                            }
                        ]
                    }
                ]
            },
            cookies={"token": self.token},
            name=f"/workout-plans/{self.workout_plan_id}",
            catch_response=True
        ) as response:

            if response.status_code == 200:
                response.success()
            else:
                print(f"Failed to update workout plan: {response.status_code} - {response.text}")
                print(self.userId, self.workout_plan_id)
                response.failure(f"Failed to update workout plan {self.workout_plan_id}: {response.status_code}")
"""

class Register(SequentialTaskSet):
    credentials = None
    current_index = 0
    lock = gevent.lock.Semaphore()

    def on_start(self):
        if Register.credentials is None:
            with open(register_data, 'r') as f:
                Register.credentials = [line.strip().split(',') for line in f.readlines()]

        with Register.lock:
            if Register.current_index >= len(Register.credentials):
                self.interrupt()
            self.username, self.password = Register.credentials[Register.current_index]
            Register.current_index += 1

    @task
    def register(self):
        data = {
            "name": self.username,
            "email": self.username,
            "password": self.password,
            "metricType": "METRIC"
        }
        response = self.client.post("/auth/register", json=data)
        if response.status_code == 201:
            print("Registration successful")
            self.interrupt() 
        else:
            print("Failed registration:", response.text)
            self.interrupt()
class TestUser(HttpUser):
    tasks = [LoginAndCheckWorkouts, LoginAndCreateWorkoutPlanAndUpdate, LoginAndCheckProgress]
    wait_time = between(1, 3)
    host = "https://api.sic-aa-fitness.xyz/api"
    #host = "http://localhost:8080/api"

