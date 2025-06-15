import os

class UtilizadorGenerator:
    def __init__(self, start_index=100):
        self.current_index = start_index

    def proximo_utilizador(self):
        email = f"usersss{self.current_index}@gmail.com"
        password = "Password123!"
        self.current_index += 1
        return email, password


gerador = UtilizadorGenerator(start_index=100)

# Gerar 1000 utilizadores
with open(f"users_to_create.csv", "w") as f:
    for _ in range(1000):
        email, password = gerador.proximo_utilizador()
        f.write(f"{email},{password}\n")
