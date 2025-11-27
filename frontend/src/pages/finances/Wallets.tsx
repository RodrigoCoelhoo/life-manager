export default function Wallets() {
	return (
		<div className="w-full p-6 text-textcolor flex flex-col gap-4">

			<button
				className="bg-primary w-fit p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
			>
				Create +
			</button>

			<div className="h-full w-full p-6 text-textcolor text-4xl rounded-t-lg grid gap-2 grid-cols-[1fr_3fr_1fr] bg-amber-300">
				<div className="bg-accent">
					a
				</div>
				<div className="bg-red-600">
					b
				</div>
				<div className="bg-white">
					c
				</div>
			</div>
		</div>
	);
}
