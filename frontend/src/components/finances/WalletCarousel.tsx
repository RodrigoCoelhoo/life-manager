import { useState } from "react";
import type { WalletResponseDTO } from "../../services/finances/wallet/wallet.dto";
import { FaArrowLeft, FaArrowRight } from "react-icons/fa";
import { LuWallet } from "react-icons/lu";
import { HiOutlineCash } from "react-icons/hi";
import { formatBalance } from "../../services/finances/currencies.type";

interface WalletCarouselProps {
	wallets?: WalletResponseDTO[];
}

export default function WalletCarousel({ wallets }: WalletCarouselProps) {
	const [currentIndex, setCurrentIndex] = useState(0);
	const total = wallets?.length ?? 0;

	if (!wallets || total === 0) {
		return (
			<div className="h-40 flex items-center justify-center text-textcolor/70 bg-background/30 rounded-lg">
				No wallets available
			</div>
		);
	}

	const prev = () => setCurrentIndex((i) => (i - 1 + total) % total);
	const next = () => setCurrentIndex((i) => (i + 1) % total);
	const currentWallet = wallets[currentIndex];

	return (
		<div className="w-full">
			{/* Container centralizado */}
			<div className="relative w-full">
				{/* Botões de navegação */}
				{total > 1 && (
					<div className="flex justify-between items-center absolute inset-x-0 top-1/2 -translate-y-1/2 z-10">
						<button
							onClick={prev}
							className="bg-foreground/80 hover:bg-background/80 p-2 rounded-full -translate-x-2 cursor-pointer"
						>
							<FaArrowLeft size={14} />
						</button>
						<button
							onClick={next}
							className="bg-foreground/80 hover:bg-background/80 p-2 rounded-full translate-x-2 cursor-pointer"
						>
							<FaArrowRight size={14} />
						</button>
					</div>
				)}

				<div className="mx-8">
					<div className="p-3 bg-background/80 rounded-lg flex flex-col transition text-textcolor shadow-lg">
						<div className="flex justify-between items-center p-1">
							{currentWallet?.type === "BANK" ? (
								<LuWallet size={32} className="bg-primary p-1 rounded-lg" />
							) : (
								<HiOutlineCash size={32} className="bg-secondary p-1 rounded-lg" />
							)}
							<span className="bg-secondary/25 rounded-xl px-2 py-1 text-xs font-extralight text-textcolor/80">
								{currentWallet?.type}
							</span>
						</div>

						<div className="p-1 flex flex-col justify-between h-full gap-1">
							<div className="flex flex-col">
								<h2 className="text-lg">{currentWallet?.name}</h2>
								<span className="font-extralight text-textcolor/80 text-sm">
									{currentWallet?.currency} Account
								</span>
							</div>

							<div className="border border-primary/40 my-1" />

							<div>
								<span className="text-textcolor/80 text-sm">Balance</span>
								<div className="text-lg">{currentWallet && formatBalance(currentWallet.balance)}</div>
							</div>
						</div>
					</div>
				</div>
			</div>

			{/* Indicadores */}
			<div className="flex flex-col items-center mt-4 gap-2">
				<div className="flex gap-2">
					{wallets.map((_, idx) => (
						<button
							key={idx}
							onClick={() => setCurrentIndex(idx)}
							className={`w-2 h-2 rounded-full transition-all ${idx === currentIndex ? "bg-primary w-6" : "bg-gray-400/50"}`}
						/>
					))}
				</div>

				{total > 1 && (
					<div className="text-sm text-textcolor/70">
						{currentIndex + 1} of {total}
					</div>
				)}
			</div>
		</div>
	);
}