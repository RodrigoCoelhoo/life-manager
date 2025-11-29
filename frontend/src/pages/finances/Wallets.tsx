import { useEffect, useState } from "react";
import type { PageResponseDTO } from "../../services/api.dto";
import Loading from "../../components/common/Loading";
import ErrorMessage from "../../components/common/Error";
import { Modal } from "../../components/common/Modal";
import { Pagination } from "../../components/common/Pagination";
import type { WalletDTO, WalletResponseDTO, WalletUpdateDTO } from "../../services/finances/wallet/wallet.dto";
import { walletService } from "../../services/finances/wallet/wallet.service";
import WalletCard from "../../components/finances/WalletCard";
import WalletForm from "../../components/finances/WalletForm";

export default function Exercises() {
	const [wallets, setWallets] = useState<WalletResponseDTO[]>([]);
	const [loading, setLoading] = useState<boolean>(true);
	const [error, setError] = useState<string | null>(null);

	const [page, setPage] = useState<number>(1);
	const [totalPages, setTotalPages] = useState<number>(1);
	const [totalElements, setTotalElements] = useState<number>(1);
	const [elementsPerPage, setElementsPerPage] = useState<number>(18);

	const [createWalletOpen, setCreateWalletOpen] = useState<boolean>(false);

	const fetchWallets = async () => {
		try {
			setError(null);
			setLoading(true);
			const data: PageResponseDTO<WalletResponseDTO> = await walletService.getAllWallets(page - 1, elementsPerPage, "");

			setWallets(data.content);
			setTotalPages(data.totalPages);
			setTotalElements(Number(data.totalElements));
		} catch (err) {
			console.error(err);
			setError("Failed to fetch wallets");
		} finally {
			setLoading(false);
		}
	};

	const createWallet = async (wallet: WalletDTO) => {
		try {
			setLoading(true);
			const data: WalletResponseDTO = await walletService.createWallet(wallet);
			setWallets(prev => {
				const updated = [data, ...prev];

				if (updated.length > elementsPerPage) {
					updated.pop();
				}

				return updated;
			});

			setTotalElements(prev => prev + 1);
		} catch (err) {
			console.error(err);
			setError("Failed to create wallet");
		} finally {
			setLoading(false);
		}
	};

	const updateWallet = async (id: number, updatedData: WalletUpdateDTO) => {
		try {
			setLoading(true);
			const updated = await walletService.updateWallet(id, updatedData);
			setWallets(prev => prev.map(e => (e.id === id ? updated : e)));
		} catch (err) {
			setError("Failed to update wallet");
		} finally {
			setLoading(false);
		}
	};

	const deleteWallet = async (id: number) => {
		try {
			setLoading(true);
			await walletService.deleteWallet(id);
			fetchWallets();
			setTotalElements(prev => prev - 1);
		} catch (err) {
			setError("Failed to delete wallet");
		} finally {
			setLoading(false);
		}
	};


	useEffect(() => {
		fetchWallets();
	}, [page, elementsPerPage]);

	if (loading) return <Loading />;
	if (error) {
		return (
			<div className="flex justify-center items-center h-full">
				<ErrorMessage
					title="Failed to load wallets"
					message="There was a problem connecting to the server. Please try again."
					onRetry={() => fetchWallets()}
				/>
			</div>
		)
	};

	return (
		<>
			<div className="w-full p-6 text-textcolor flex flex-col gap-4">

				<div className="flex items-center justify-between gap-4">
					<button
						className="bg-primary p-2 px-4 rounded-xl cursor-pointer hover:bg-primary/80 font-semibold"
						onClick={() => setCreateWalletOpen(true)}
					>
						Create +
					</button>

					<div className="flex items-center gap-3">
						<div className="flex gap-3 items-center">
							<label htmlFor="walletsPerPage" className="text-sm mb-1 font-extralight">
								Wallets per page
							</label>

							<select
								id="walletsPerPage"
								name="walletsPerPage"
								required
								className="form-input w-14"
								value={elementsPerPage}
								onChange={(e) => setElementsPerPage(Number(e.target.value))}
							>
								<option value="12">12</option>
								<option value="18">18</option>
								<option value="24">24</option>
							</select>
						</div>

						<span className="text-secondary"> {totalElements} {totalElements === 1 ? "wallet" : "wallets"} </span>
					</div>
				</div>

				<div className="flex flex-col gap-2">
					<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
						{wallets.map((wallet) => (
							<WalletCard
								key={wallet.id}
								{...wallet}
								onUpdate={updateWallet}
								onDelete={deleteWallet}
							/>
						))}
					</div>
				</div>

				<div className="mb-4">
					<Pagination
						currentPage={page}
						totalPages={totalPages}
						onPageChange={(p: number) => setPage(p)}
					/>
				</div>
			</div>

			<Modal isOpen={createWalletOpen} onClose={() => setCreateWalletOpen(false)}>
				<WalletForm
					onClose={() => setCreateWalletOpen(false)}
					onCreate={createWallet}
				/>
			</Modal>
		</>
	);
}
