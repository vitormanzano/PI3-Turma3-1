// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAnalytics } from "firebase/analytics";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
  apiKey: "AIzaSyB6JHWa6q9nIyoox1p_jbN9am38bK1o_Nc",
  authDomain: "siteparceiro-dd0f0.firebaseapp.com",
  projectId: "siteparceiro-dd0f0",
  storageBucket: "siteparceiro-dd0f0.firebasestorage.app",
  messagingSenderId: "883964334664",
  appId: "1:883964334664:web:0d4063de7a2869e173fe7d",
  measurementId: "G-PW56MG2MRV"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);    