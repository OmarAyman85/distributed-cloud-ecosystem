import requests
import uuid
import json

BASE_URL = "http://localhost:8081/api"

def generate_user():
    uid = str(uuid.uuid4())[:8]
    return {
        "username": f"user_{uid}",
        "email": f"user_{uid}@example.com",
        "password": "Password123!",
        "firstName": "Test",
        "lastName": "User",
        "birthDate": "2000-01-01T00:00:00Z",
        "mobilePhone": "1234567890",
        "gender": "MALE",
        "mfaEnabled": False
    }

def test_flow():
    user = generate_user()
    print(f"Testing with user: {user['username']}")

    # 1. Register
    print("1. Registering...")
    try:
        reg_response = requests.post(f"{BASE_URL}/register", json=user)
        print(f"   Status: {reg_response.status_code}")
        print(f"   Body: {reg_response.text}")
        if reg_response.status_code != 200:
            print("   FAILED to register")
            return
    except Exception as e:
        print(f"   Exception during register: {e}")
        return

    # 2. Login
    print("2. Logging in...")
    login_payload = {
        "identifier": user["username"],
        "password": user["password"]
    }
    try:
        login_response = requests.post(f"{BASE_URL}/login", json=login_payload)
        print(f"   Status: {login_response.status_code}")
        if login_response.status_code == 200:
            data = login_response.json()
            access_token = data.get("accessToken")
            print(f"   Login SUCCESS. Access Token present: {bool(access_token)}")
            if access_token:
                print(f"   Token: {access_token[:20]}...")
        else:
            print(f"   Login FAILED: {login_response.text}")
    except Exception as e:
         print(f"   Exception during login: {e}")

if __name__ == "__main__":
    test_flow()
