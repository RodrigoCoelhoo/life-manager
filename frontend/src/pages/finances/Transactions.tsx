export default function Transactions() {
	return (
		<>
			<div className="h-full w-full p-6 text-textcolor text-4xl rounded-lg flex flex-col gap-6">
				
				<div className="flex items-center rounded-lg gap-6">
					<button className="bg-foreground p-2 px-5">Create</button>
					<button className="bg-foreground p-2 px-5">Delete</button>
				</div>

				<div className="bg-foreground flex justify-center items-center rounded-lg">
					Container 2
				</div>
			</div>
		</>
	);
}