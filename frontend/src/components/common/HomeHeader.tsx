import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../../contexts/AuthContext";

export default function HomeHeader() {
  const { isLoggedIn, username, logout } = useAuth();

  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/home");
  }

  return (
    <header className="bg-foreground fixed top-0 left-0 w-full drop-shadow-[0_4px_6px_rgba(0,0,0,0.2)] flex flex-row justify-between items-center h-16 px-10 lg:px-20 z-50">
      <h1 className="text-textcolor text-2xl font-semibold tracking-tight cursor-default">
        Life<span className="text-primary">Manager</span>
      </h1>

      {isLoggedIn ? (
        <div className="flex items-center gap-4">
          <span className="text-white">Hello, {username}</span>
          <button
            onClick={handleLogout}
            className="bg-background border-2 border-secondary/80 hover:border-primary/80 text-white rounded-xl h-8 w-24 transition-all duration-300 shadow-none hover:shadow-[0_0_12px] hover:shadow-secondary hover:text-secondary"
          >
            Logout
          </button>
        </div>
      ) : (
        <Link
          to="/login"
          className="bg-background border-2 items-center text-center border-secondary/80 hover:border-primary/80 text-white rounded-xl h-8 w-24 transition-all duration-300 shadow-none hover:shadow-[0_0_12px] hover:shadow-secondary hover:text-secondary"
        >
          Log In
        </Link>
      )}
    </header>
  );
}
