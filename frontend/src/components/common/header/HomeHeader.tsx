import { Link } from "react-router-dom";
import logo from "../../../assets/logo.png"

export default function HomeHeader() {

  return (
    <header className="bg-foreground fixed top-0 left-0 w-full drop-shadow-[0_4px_6px_rgba(0,0,0,0.2)] flex flex-row justify-between items-center h-16 px-10 lg:px-20 z-50">
      <div>
        <img src={logo} alt="LifeManager Logo" className="h-16" />
      </div>

        <Link
          to="/login"
          className="bg-background border-2 items-center text-center border-secondary/80 hover:border-primary/80 text-white rounded-xl h-8 w-24 transition-all duration-300 shadow-none hover:shadow-[0_0_12px] hover:shadow-secondary hover:text-secondary"
        >
          Log In
        </Link>
    </header>
  );
}
