Write-Output "Welcome to ClipCraze Startup..."

function Start-Backend {
    try {
        Write-Output "Starting DockerDB"
        docker start mongodb
        Write-Output "DockerDB Running!"
        Write-Output "Compiling jar file."
        cd backend
        mvn package install
        Write-Output "Done! Running backend"
        
        cd target
        java -jar backend-1.0-SNAPSHOT.jar



    } catch {
        Write-Output "Error starting DockerDB: $_"
    }
}

function Start-Frontend {
    try {
        cd .. 
        cd .. 
        cd frontend

        npm run serve
    } catch {
        Write-Output "Error starting frontend: $_"
    }
}

# Start the backend and frontend services
Start-Backend
Start-Frontend
