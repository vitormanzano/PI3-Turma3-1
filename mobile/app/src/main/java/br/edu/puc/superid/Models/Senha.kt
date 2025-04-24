package br.edu.puc.superid.Models

data class Senha (
    var guid: String,
    var login: String,
    var nomeCategoria: String,
    var senha: String,
    var accessToken: String,
    var uidUsuario: String
)
