export default function Header() {
	return (
		<header className="bg-foreground fixed top-0 left-0 w-full drop-shadow-[0_4px_6px_rgba(0,0,0,0.2)] flex flex-row justify-between items-center h-16 px-20 z-50">
			<div className="flex items-center justify-center xl:justify-start w-full xl:w-auto mb-2 xl:mb-0">
				<h1 className="text-textcolor text-2xl font-semibold tracking-tight cursor-default">
					Life<span className="text-primary">Manager</span>
				</h1>
			</div>
		</header>
	);
}